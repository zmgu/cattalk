package com.ex.backend.security.jwt.provider;

import com.ex.backend.security.jwt.constants.JwtConstants;
import com.ex.backend.security.jwt.props.JwtProps;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {

    private final JwtProps jwtProps;

    private SecretKey secretKey;

    public JwtProvider(JwtProps jwtProps) {
        this.jwtProps = jwtProps;
        this.secretKey = new SecretKeySpec(jwtProps.getSecretKey().getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createToken(String category, Long userId, String nickname, String role) {
        long expiredTime =
                category.equals(JwtConstants.ACCESS_TOKEN) ? jwtProps.getAccessExpiredTime() : jwtProps.getRefreshExpiredTime();

        // token 토큰 생성
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

    public Long getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", Long.class);
    }

    public String getNickname(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("nickname", String.class);
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

    public Authentication getAuthentication(String token) {
        token = token.substring(7).trim();
        Long userId = getUserId(token);
        String role = getRole(token);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        System.out.println("token = " + token);
        return new UsernamePasswordAuthenticationToken(userId, null, authorities);
    }
}