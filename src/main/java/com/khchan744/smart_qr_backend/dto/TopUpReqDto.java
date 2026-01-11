package com.khchan744.smart_qr_backend.dto;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class TopUpReqDto {
    private final String topUpAmount;

    public TopUpReqDto(@NonNull String topUpAmount) {
        this.topUpAmount = topUpAmount;
    }

}
