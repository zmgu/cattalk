package com.ex.backend.redis;

import com.ex.backend.security.jwt.dto.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    private String generateKey(String refreshToken) {
        return "refreshToken:" + refreshToken;
    }

    public void createRefreshToken(RefreshToken refreshToken) {
        String key = generateKey(refreshToken.getRefreshToken());
        redisTemplate.opsForValue().set(key, refreshToken.getUserId());
    }

    public boolean existRefreshToken(String refreshToken) {
        String key = generateKey(refreshToken);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteRefreshToken(String refreshToken) {
        String key = generateKey(refreshToken);
        redisTemplate.delete(key);
    }
}
