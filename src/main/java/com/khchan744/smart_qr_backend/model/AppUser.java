package com.khchan744.smart_qr_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    private String username;
    private String password;
    private String secret;
    private Float balance;

    @JsonIgnore
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<History> history = new ArrayList<>();

    @JsonIgnore
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "app_user_used_tokens", joinColumns = @JoinColumn(name = "username"))
    @Column(name = "token", nullable = false, length = 2048)
    private List<String> usedPaymentTokens = new ArrayList<>();

    public AppUser(String username, String password, String secret, Float balance) {
        this.username = username;
        this.password = password;
        this.secret = secret;
        this.balance = balance;
    }

    public void addHistory(History newHistory) {
        if (newHistory == null) return;
        this.history.add(newHistory);
    }

    public void addUsedPaymentToken(String token) {
        if (token == null) return;
        this.usedPaymentTokens.add(token);
    }

    public boolean hasUsedPaymentToken(String token) {
        return token != null && this.usedPaymentTokens.contains(token);
    }
}
