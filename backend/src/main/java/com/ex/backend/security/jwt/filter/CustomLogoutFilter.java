package com.ex.backend.security.jwt.filter;

import com.ex.backend.redis.RefreshTokenService;
import com.ex.backend.security.jwt.constants.JwtConstants;
import com.ex.backend.security.jwt.cookie.CookieUtil;
import com.ex.backend.security.jwt.provider.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtil cookieUtil;
    private final Logger logger = Logger.getLogger(CustomLogoutFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        // POST 방식으로 /logout 요청이 아닐 시 다음 필터로 이동
        if (!("/logout".equals(requestUri) && "POST".equals(requestMethod))) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = cookieUtil.getCookieValue(request, JwtConstants.REFRESH_TOKEN);

        // 리프래시 토큰 null 체크
        if (refreshToken == null) {
            logger.info("refreshToken = null");
            logger.info("로그아웃 진행");

            logoutSetting(response);

            return;
        }

        // 리프래시 토큰 만료 체크
        try {
            jwtProvider.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            logger.info("refreshToken 만료됨 ");
            logger.info("로그아웃 진행");

            logoutSetting(response);

            return;
        }

        // DB에 저장되어 있는지 확인
        Boolean isExist = refreshTokenService.existRefreshToken(refreshToken);
        if (!isExist) {
            logger.info("refreshToken이 DB에 존재하지 않음 ");
            logger.info("로그아웃 진행");

            logoutSetting(response);

            return;
        }

        //Redis에서 리프래시 토큰 제거
        refreshTokenService.deleteRefreshToken(refreshToken);

        // 쿠키에서 리프래시 토큰 제거
        logoutSetting(response);
    }

    private HttpServletResponse logoutSetting(HttpServletResponse response) {

        // 쿠키에서 리프래시 토큰 제거
        Cookie cookie = cookieUtil.deleteCookie(JwtConstants.REFRESH_TOKEN);
        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);

        return response;
    }
}