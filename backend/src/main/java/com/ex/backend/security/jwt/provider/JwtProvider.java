package com.ex.backend.security.jwt.provider;

import com.ex.backend.security.jwt.props.JwtProps;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

@Component
public class JwtProvider {

    private final JwtProps jwtProps;

    private SecretKey secretKey;

    public JwtProvider(JwtProps jwtProps) {
        this.jwtProps = jwtProps;
        this.secretKey = new SecretKeySpec(jwtProps.getSecretKey().getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createToken(String category,String username, String role, Long expiredTime) {

        // token 토큰 생성
        String token = Jwts.builder()
                .signWith(secretKey)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredTime))
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .compact();

        return token;
    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

}