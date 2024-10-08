package com.ex.backend.security.jwt.provider;

import com.ex.backend.security.jwt.constants.JwtConstants;
import com.ex.backend.security.jwt.props.JwtProps;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final JwtProps jwtProps;

    private final SecretKey secretKey;

    public JwtProvider(JwtProps jwtProps) {
        this.jwtProps = jwtProps;
        this.secretKey = new SecretKeySpec(jwtProps.getSecretKey().getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // token 토큰 생성
    public String createToken(String category, Long userId, String nickname, String role) {
        long expiredTime =
                category.equals(JwtConstants.ACCESS_TOKEN) ? jwtProps.getAccessExpiredTime() : jwtProps.getRefreshExpiredTime();

        String token = Jwts.builder()
                .signWith(secretKey)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredTime))
                .claim("userId", userId)
                .claim("nickname", nickname)
                .claim("role", role)
                .compact();

        return category.equals(JwtConstants.ACCESS_TOKEN) ? JwtConstants.TOKEN_PREFIX + token : token;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(removePrefix(token))
                .getPayload();
    }

    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    public String getNickname(String token) {
        return parseClaims(token).get("nickname", String.class);
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    public String removePrefix(String token) {
        return token.startsWith(JwtConstants.TOKEN_PREFIX) ? token.substring(7).trim() : token;
    }
}