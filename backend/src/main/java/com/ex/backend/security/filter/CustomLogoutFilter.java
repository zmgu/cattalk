package com.ex.backend.security.filter;

import com.ex.backend.security.jwt.service.RefreshTokenRedis;
import com.ex.backend.security.jwt.constants.JwtConstants;
import com.ex.backend.security.cookie.CookieUtil;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRedis refreshTokenRedis;
    private final CookieUtil cookieUtil;

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
            log.info("refreshToken = null, 로그아웃 진행");
            logoutSetting(response);
            return;
        }

        // 리프래시 토큰 만료 체크
        try {
            jwtProvider.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            log.info("refreshToken 만료됨, 로그아웃 진행");
            logoutSetting(response);
            return;
        }

        // DB에 저장되어 있는지 확인
        Boolean isExist = refreshTokenRedis.existRefreshToken(refreshToken);

        if (!isExist) {
            log.info("refreshToken이 DB에 존재하지 않음, 로그아웃 진행");
            logoutSetting(response);
            return;
        }

        //Redis에서 리프래시 토큰 제거
        refreshTokenRedis.deleteRefreshToken(refreshToken);

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