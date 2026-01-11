package com.khchan744.smart_qr_backend.service;

import com.khchan744.smart_qr_backend.dto.*;
import com.khchan744.smart_qr_backend.model.AppUser;
import com.khchan744.smart_qr_backend.model.History;
import com.khchan744.smart_qr_backend.model.HistoryType;
import com.khchan744.smart_qr_backend.repo.AppUserRepo;
import com.khchan744.smart_qr_backend.utils.Utils;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AppUserService {
    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JwtService jwtService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AppUserRepo appUserRepo;

    @Autowired
    SecurityService securityService;

    private static final int MIN_BALANCE = 500;
    private static final int MAX_BALANCE = 1001;

    private static final String LOGIN_SUCCESS_RESP = "Login success!";
    private static final String LOGIN_FAIL_RESP = "Incorrect username or password!";
    private static final String REG_FAIL_RESP = "Username already exists.";
    private static final String REG_SUCCESS_RESP = "Registration successful. Please log in with your new account.";
    private static final String GET_BALANCE_FAIL_RESP = "Cannot retrieve balance information as user not found.";
    private static final String VER_PAYMENT_USER_NOT_FOUND_RESP = "Fail to verify payment as payer or payee's username not found.";
    private static final String VER_PAYMENT_SAME_USER_RESP = "Payment unsuccessful! Payer's and payee's username cannot be the same.";
    private static final String VER_PAYMENT_INSUFF_BALANCE_RESP = "Payment cannot proceed due to insufficient balance.";
    private static final String VER_PAYMENT_INVALID_TOKEN_RESP = "Payment unsuccessful! Fail to verify payment token.";
    private static final String VER_PAYMENT_SUCCESS_RESP_FORMAT = "Payment success! You have received HK$%.1f from %s.";
    private static final String VER_PAYMENT_REPLAY_TOKEN_RESP = "Payment unsuccessful! The payment token has been used already.";


    public LoginRespDto verifyLogin(@NonNull CredentialsDto credentials) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
        if (authentication.isAuthenticated()) {
            AppUser appUser = appUserRepo.findByUsername(authentication.getName());
            return new LoginRespDto(RespStatus.SUCCESS, LOGIN_SUCCESS_RESP,
                    jwtService.generateToken(credentials.getUsername()), appUser.getSecret());
        }
        return new LoginRespDto(RespStatus.FAILURE, LOGIN_FAIL_RESP, null, null);
    }

    public StandardRespDto register(@NonNull CredentialsDto credentials) {
        AppUser existingUser = appUserRepo.findByUsername(credentials.getUsername());
        if (existingUser != null) {
            return new StandardRespDto(RespStatus.FAILURE, REG_FAIL_RESP);
        }
        String encodedPassword = passwordEncoder.encode(credentials.getPassword());
        String secret = Utils.base64Encode(Utils.sha256Hash(encodedPassword.getBytes(StandardCharsets.UTF_8)));
        float balance = (float) ThreadLocalRandom.current().nextInt(MIN_BALANCE, MAX_BALANCE);
        appUserRepo.save(new AppUser(credentials.getUsername(), encodedPassword, secret, balance));
        return new StandardRespDto(RespStatus.SUCCESS, REG_SUCCESS_RESP);
    }

    public StandardRespDto getBalance() {
        AppUser appUser = appUserRepo.findByUsername(securityService.getUsername());
        if (appUser != null) {
            return new StandardRespDto(RespStatus.SUCCESS, String.valueOf(appUser.getBalance()));
        }
        return new StandardRespDto(RespStatus.FAILURE, GET_BALANCE_FAIL_RESP);
    }

    @Transactional
    public StandardRespDto verifyPayment(@NonNull VerifyPaymentDto verifyPaymentDto) {
        // check if payer exists
        String payerUsername = verifyPaymentDto.getPayerUsername();
        String payeeUsername = securityService.getUsername();
        AppUser payer = appUserRepo.findByUsername(payerUsername);
        AppUser payee = appUserRepo.findByUsername(payeeUsername);
        if (payer == null || payee == null) {
            return new StandardRespDto(RespStatus.FAILURE, VER_PAYMENT_USER_NOT_FOUND_RESP);
        }
        if(payerUsername.equals(payeeUsername)) {
            return new StandardRespDto(RespStatus.FAILURE, VER_PAYMENT_SAME_USER_RESP);
        }
        // check if token has been used already
        String postedPaymentToken = verifyPaymentDto.getPaymentToken();
        if (payer.hasUsedPaymentToken(postedPaymentToken)) {
            return new StandardRespDto(RespStatus.FAILURE, VER_PAYMENT_REPLAY_TOKEN_RESP);
        }
        // check if payer has enough balance
        float payerBalance = payer.getBalance();
        float paymentAmount = Float.parseFloat(verifyPaymentDto.getPaymentAmount());
        if (payerBalance < paymentAmount) {
            return new StandardRespDto(RespStatus.FAILURE, VER_PAYMENT_INSUFF_BALANCE_RESP);
        }

        // start verifying payment token
        byte[] receivedPaymentTokenBytes = Utils.base64Decode(verifyPaymentDto.getPaymentToken());
        byte[] payerUsernameBytes = payerUsername.getBytes(StandardCharsets.UTF_8);
        byte[] payerSecret = Utils.base64Decode(payer.getSecret());
        byte[] paymentAmountBytes = Utils.paymentAmountToBytes(verifyPaymentDto.getPaymentAmount());
        byte[] payeeMetadataFingerprintBytes = Utils.base64Decode(verifyPaymentDto.getPayeeMetadataFingerprint());
        byte[] recomputedPaymentToken = Utils.computePaymentToken(
                payerUsernameBytes, payerSecret,
                paymentAmountBytes, payeeMetadataFingerprintBytes);

        if (Arrays.equals(receivedPaymentTokenBytes, recomputedPaymentToken)) {
            // both tokens are the same, verification successful
            float payerNewBalance = Float.parseFloat(String.format(Locale.US, "%.1f", payerBalance - paymentAmount));
            float payeeNewBalance = Float.parseFloat(String.format(Locale.US, "%.1f", payee.getBalance() + paymentAmount));
            payer.setBalance(payerNewBalance);
            payee.setBalance(payeeNewBalance);

            // update transaction history for payer and payee
            Instant now = Instant.now();
            payer.addHistory(new History(payer, now, HistoryType.PAY, verifyPaymentDto.getPaymentAmount(), payeeUsername));
            payee.addHistory(new History(payee, now, HistoryType.RECEIVE, verifyPaymentDto.getPaymentAmount(), payerUsername));
            // add used token
            payer.addUsedPaymentToken(postedPaymentToken);

            return new StandardRespDto(RespStatus.SUCCESS,
                    String.format(VER_PAYMENT_SUCCESS_RESP_FORMAT, paymentAmount, payerUsername));
        }

        return new StandardRespDto(RespStatus.FAILURE, VER_PAYMENT_INVALID_TOKEN_RESP);
    }


    @Transactional
    public StandardRespDto topUp(TopUpReqDto topUpReqDto) {
        AppUser appUser = appUserRepo.findByUsername(securityService.getUsername());
        if (appUser != null) {
            float sum = Float.parseFloat(String.format(Locale.US, "%.1f",
                    appUser.getBalance() + Float.parseFloat(topUpReqDto.getTopUpAmount())));
            appUser.setBalance(sum);

            // add a TOP_UP history record
            appUser.addHistory(new History(appUser, Instant.now(), HistoryType.TOP_UP, topUpReqDto.getTopUpAmount(), null));

            // appUserRepo.save(appUser);
            return new StandardRespDto(RespStatus.SUCCESS, null);
        }
        return new StandardRespDto(RespStatus.FAILURE, null);
    }

    public HistoryRespDto getHistory() {
        AppUser appUser = appUserRepo.findByUsername(securityService.getUsername());
        if (appUser != null) {
            List<History> history = appUser.getHistory();
            return new HistoryRespDto(RespStatus.SUCCESS, history.size() + " record(s)", history);
        }
        return new HistoryRespDto(RespStatus.FAILURE, "User not found.", new ArrayList<>());
    }
}
