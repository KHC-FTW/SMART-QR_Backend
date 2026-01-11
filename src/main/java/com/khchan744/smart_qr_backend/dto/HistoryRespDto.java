package com.khchan744.smart_qr_backend.dto;

import com.khchan744.smart_qr_backend.model.History;
import lombok.Getter;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Getter
public class HistoryRespDto extends StandardRespDto {
    private final List<History> history;
    public HistoryRespDto(@NonNull RespStatus status,
                          @Nullable String response,
                          @NonNull List<History> history) {
        super(status, response);
        this.history = history;
    }
}
