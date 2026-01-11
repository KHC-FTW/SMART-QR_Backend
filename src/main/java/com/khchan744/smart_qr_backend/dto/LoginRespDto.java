package com.khchan744.smart_qr_backend.dto;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class LoginRespDto extends StandardRespDto {
    private final String jwt;
    private final String secret;

    public LoginRespDto(@NonNull RespStatus status,
                        @Nullable String response,
                        @Nullable String jwt,
                        @Nullable String secret) {
        super(status, response);
        this.jwt = jwt == null ? "" : jwt;
        this.secret = secret == null ? "" : secret;
    }
}
