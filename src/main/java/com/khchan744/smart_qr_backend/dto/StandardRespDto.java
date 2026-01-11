package com.khchan744.smart_qr_backend.dto;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class StandardRespDto {
    private final String status;
    private final String response;

    public StandardRespDto(@NonNull RespStatus status,
                           @Nullable String response) {
        this.status = status == RespStatus.SUCCESS ? "success" : "failure";
        this.response = response == null ? "" : response;
    }
}
