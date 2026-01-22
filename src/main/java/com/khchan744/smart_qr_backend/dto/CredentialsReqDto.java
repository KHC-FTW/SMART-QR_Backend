package com.khchan744.smart_qr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CredentialsReqDto {
    private final String username;
    private final String password;
}
