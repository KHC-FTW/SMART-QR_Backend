package com.khchan744.smart_qr_backend.controller;

import com.khchan744.smart_qr_backend.dto.*;
import com.khchan744.smart_qr_backend.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppUserController {
    @Autowired
    private AppUserService appUserService;

    @GetMapping("/test/hello-world")
    public String helloWorld() {
        return "Hello World!";
    }

    @PostMapping("/log-in")
    public LoginRespDto logIn(@RequestBody CredentialsDto credentialsDto) {
        return appUserService.verifyLogin(credentialsDto);
    }

    @PostMapping("/register")
    public StandardRespDto signUp(@RequestBody CredentialsDto credentialsDto) {
        return appUserService.register(credentialsDto);
    }

    @GetMapping("/get-balance")
    public StandardRespDto getBalance() {
        return appUserService.getBalance();
    }

    @PostMapping("/verify-payment")
    public StandardRespDto verifyPayment(@RequestBody VerifyPaymentDto verifyPaymentDto) {
        return appUserService.verifyPayment(verifyPaymentDto);
    }

    @PostMapping("/top-up")
    public StandardRespDto topUp(@RequestBody TopUpReqDto topUpReqDto) {
        return appUserService.topUp(topUpReqDto);
    }

    @GetMapping("/get-history")
    public HistoryRespDto getTransactionHistory() {
        return appUserService.getHistory();
    }
}
