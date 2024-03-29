package com.ex.backend.security.jwt.service;

import com.ex.backend.redis.RefreshTokenService;
import com.ex.backend.security.jwt.constants.JwtConstants;
import com.ex.backend.security.jwt.cookie.CookieUtil;
import com.ex.backend.security.jwt.dto.RefreshToken;
import com.ex.backend.security.jwt.provider.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtil cookieUtil;
    private final Logger logger = Logger.getLogger(ReissueService.class.getName());

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = cookieUtil.getCookieValue(request, JwtConstants.REFRESH_TOKEN);

        if (refreshToken == null) {
            logger.info(" refreshToken null ");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            jwtProvider.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            logger.info(" refreshToken 만료됨 ");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshTokenService.existRefreshToken(refreshToken);
        if (!isExist) {
            logger.info(" 저장되어 있는 refreshToken이 아님 ");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Long userId = jwtProvider.getUserId(refreshToken);
        String name = jwtProvider.getName(refreshToken);
        String role = jwtProvider.getRole(refreshToken);

        // 엑세스 토큰, 리프래시 토큰 생성
        String accessToken = jwtProvider.createToken(JwtConstants.ACCESS_TOKEN, userId, name, role);
        String newRefreshToken = jwtProvider.createToken(JwtConstants.REFRESH_TOKEN, userId, name, role);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshTokenService.deleteRefreshToken(refreshToken);
        refreshTokenService.createRefreshToken(new RefreshToken(newRefreshToken, String.valueOf(userId)));

        //response
        response.setHeader(JwtConstants.AUTHORIZATION, accessToken);
        response.addCookie(cookieUtil.createCookie(JwtConstants.REFRESH_TOKEN, newRefreshToken));

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
