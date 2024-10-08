package com.ex.backend.security.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 60*60)
@AllArgsConstructor
public class RefreshToken {

    @Id
    private String refreshToken;
    private String userId;

}
