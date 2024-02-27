package com.ex.backend.redis;

import com.ex.backend.security.jwt.dto.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

//    Optional<RefreshToken> findByAccessToken(String accessToken, String username);
}

