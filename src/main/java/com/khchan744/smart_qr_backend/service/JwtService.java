package com.khchan744.smart_qr_backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private SecretKey jwtSecret;

    @Value("${jwt.secret}")
    private String jwtSecretString;

    @Value("${jwt.expiration-time}")
    private Long jwtExpirationTime;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims()
                .add(claims)
                .issuer(jwtIssuer)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .and()
                .signWith(getJwtSecret())
                .compact();
    }

    private SecretKey getJwtSecret() {
        if (jwtSecret == null) {
            byte[] keyBytes;
            try {
                keyBytes = Decoders.BASE64.decode(jwtSecretString);
            } catch (IllegalArgumentException ex) {
                keyBytes = jwtSecretString.getBytes(StandardCharsets.UTF_8);
            }
            jwtSecret = Keys.hmacShaKeyFor(keyBytes);
        }
        return jwtSecret;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final Claims claims = extractAllClaims(token);
            final String username = claims.getSubject();
            final String issuer = claims.getIssuer();
            final Date exp = claims.getExpiration();

            return username != null
                    && username.equals(userDetails.getUsername())
                    && jwtIssuer.equals(issuer)
                    && exp != null
                    && exp.after(new Date(System.currentTimeMillis()));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getJwtSecret())
                .requireIssuer(jwtIssuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
