package com.ex.backend.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@Getter @Setter
@RedisHash(value = "refreshToken", timeToLive = 60*60)
@AllArgsConstructor
public class RefreshToken {

    @Id
    private String refreshToken;
    private String username;

}
