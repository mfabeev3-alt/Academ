package ru.shatskikh.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;


@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7;

    public String generateToken(Long telegramUserId, Long groupId) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(String.valueOf(telegramUserId)) // Telegram ID — это основной субъект
                .claim("groupId", groupId)
                .issuedAt(new Date(now))
                .expiration(new Date(now + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    public Long getTelegramUserIdFromToken(String token) {
        String subject = extractAllClaims(token).getSubject();
        return Long.valueOf(subject);
    }

    public Long getGroupIdFromToken(String token) {
        // Извлекаем как Number, чтобы не упасть с ClassCastException (Integer vs Long)
        Number groupId = extractAllClaims(token).get("groupId", Number.class);
        return groupId != null ? groupId.longValue() : null;

    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
