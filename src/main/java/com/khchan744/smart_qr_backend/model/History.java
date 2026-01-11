package com.khchan744.smart_qr_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "history")
public class History {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_username", nullable = false)
    private AppUser creator;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d MMM uuuu hh:mm a", timezone = "Asia/Hong_Kong")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(nullable = false, length = 512)
    private String description;

    public History(@NonNull AppUser creator, @NonNull Instant createdAt, @NonNull HistoryType type,
                   @NonNull String amount, @Nullable String username) {
        this.creator = creator;
        this.createdAt = createdAt;
        this.description = switch (type) {
            case PAY -> "Paid HK$" + amount + " to " + username + ".";
            case RECEIVE -> "Received HK$" + amount + " from " + username + ".";
            case TOP_UP -> "Topped up HK$" + amount + ".";
        };
    }
}
