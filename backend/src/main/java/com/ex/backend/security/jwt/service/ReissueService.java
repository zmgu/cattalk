package com.ex.backend.security.jwt.service;

import com.ex.backend.redis.RefreshTokenService;
import com.ex.backend.security.jwt.dto.RefreshToken;
import com.ex.backend.security.jwt.provider.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
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
    private final Logger logger = Logger.getLogger(ReissueService.class.getName());

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        logger.info("==== ReissueService 시작 ====");
        //get refresh token
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            logger.info("No cookies found.");
            return new ResponseEntity<>("No cookies found.", HttpStatus.BAD_REQUEST);
        }

        for (Cookie cookie : cookies) {
            logger.info(" cookie.getName() : " + cookie.getName());
            if (cookie.getName().equals("RefreshToken")) {

                refreshToken = cookie.getValue();
                logger.info(" refreshToken : " + refreshToken);

            }
        }

        if (refreshToken == null) {
            logger.info(" refreshToken null ");
            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        try {
            jwtProvider.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            logger.info(" refreshToken 만료됨 ");

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtProvider.getCategory(refreshToken);

        if (!category.equals("RefreshToken")) {
            logger.info(" refreshToken 카테고리가 아님 ");

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshTokenService.existRefreshToken(refreshToken);
        if (!isExist) {
            logger.info(" 저장되어 있는 refreshToken이 아님 ");

            //response body
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtProvider.getUsername(refreshToken);
        String role = jwtProvider.getRole(refreshToken);

        //make new JWT
        String accessToken = jwtProvider.createToken("accessToken", username, role, 600000L);
        String newRefreshToken = jwtProvider.createToken("refreshToken", username, role, 86400000L);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshTokenService.deleteRefreshToken(refreshToken);
        refreshTokenService.createRefreshToken(new RefreshToken(newRefreshToken, username));

        //response
        response.setHeader("accessToken", accessToken);
        response.addCookie(createCookie("refreshToken", newRefreshToken));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}
