package com.ex.backend.security.jwt.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@Getter @Setter
@RedisHash(value = "refreshToken", timeToLive = 60*60)
public class RefreshToken {

    private UUID id;
    private String refreshToken;
    private String username;

    public RefreshToken(String refreshToken, String username) {
        this.id = UUID.randomUUID();
        this.refreshToken = refreshToken;
        this.username = username;
    }
}
