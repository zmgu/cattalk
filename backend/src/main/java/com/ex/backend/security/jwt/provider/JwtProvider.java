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

    private SecretKey secretKey;

    public JwtProvider(JwtProps jwtProps) {
        this.jwtProps = jwtProps;
        this.secretKey = new SecretKeySpec(jwtProps.getSecretKey().getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createToken(String category, Long userId, String name, String role) {
        long expiredTime =
                category.equals(JwtConstants.ACCESS_TOKEN) ? jwtProps.getAccessExpiredTime() : jwtProps.getRefreshExpiredTime();

        // token 토큰 생성
        String token = Jwts.builder()
                .signWith(secretKey)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredTime))
                .claim("userid", userId)
                .claim("name", name)
                .claim("role", role)
                .compact();

        return category.equals(JwtConstants.ACCESS_TOKEN) ? JwtConstants.TOKEN_PREFIX + token : token;
    }

    public Long getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", Long.class);
    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getName(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("name", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

}