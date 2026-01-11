package com.khchan744.smart_qr_backend;

import com.khchan744.smart_qr_backend.dto.VerifyPaymentDto;
import com.khchan744.smart_qr_backend.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.nio.charset.StandardCharsets;

//@SpringBootTest
class SmartQrBackendApplicationTests {


	@Test
	void testVerifyPayment() {
		// simulate client generates payload on the frontend
		byte[] mutualMFBytes = Utils.sha256Hash("testMF".getBytes(StandardCharsets.UTF_8));
		String payerUsername = "payerChan";
		byte[] payerUsernameBytes = payerUsername.getBytes(StandardCharsets.UTF_8);
		byte[] payerSecret = Utils.base64Decode("TuBcfTWsscuF3y98aGyw5xOzgQT9vFr24P/oNd2MVt8=");
		String paymentAmount = "123.4";
		byte[] paymentAmountBytes = Utils.paymentAmountToBytes(paymentAmount);
		byte[] paymentTokenBytes = Utils.computePaymentToken(payerUsernameBytes, payerSecret, paymentAmountBytes,  mutualMFBytes);

		// prepare response body for post request
		String paymentTokenBase64 = Utils.base64Encode(paymentTokenBytes);
		String payeeMfBase64 = Utils.base64Encode(mutualMFBytes);
		System.out.println("paymentTokenBase64: " + paymentTokenBase64);
		System.out.println("payeeMfBase64: " + payeeMfBase64);
		// assume server receives response body from endpoint
		VerifyPaymentDto verifyPaymentDto = new VerifyPaymentDto(paymentTokenBase64, payerUsername, paymentAmount, payeeMfBase64);

		// assume payer and payee exist and there is enough balance
		// start verifying payment token
		byte[] receivedPaymentTokenBytes = Utils.base64Decode(verifyPaymentDto.getPaymentToken());
		byte[] receivedPayerUsernameBytes = payerUsername.getBytes(StandardCharsets.UTF_8);
		// byte[] payerHashedPwBytes = new BCryptPasswordEncoder().encode("testPw").getBytes(StandardCharsets.UTF_8);
		byte[] receivedPaymentAmountBytes = Utils.paymentAmountToBytes(verifyPaymentDto.getPaymentAmount());
		byte[] payeeMetadataFingerprintBytes = Utils.base64Decode(verifyPaymentDto.getPayeeMetadataFingerprint());
		byte[] recomputedPaymentToken = Utils.computePaymentToken(receivedPayerUsernameBytes, payerSecret, receivedPaymentAmountBytes, payeeMetadataFingerprintBytes);

		Assertions.assertArrayEquals(receivedPaymentTokenBytes, recomputedPaymentToken);

	}

}
