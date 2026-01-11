package com.khchan744.smart_qr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifyPaymentDto {
    private final String paymentToken;
    private final String payerUsername;
    private final String paymentAmount;
    private final String payeeMetadataFingerprint;

}
