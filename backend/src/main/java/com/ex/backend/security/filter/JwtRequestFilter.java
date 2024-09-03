package com.ex.backend.security.filter;

import com.ex.backend.security.jwt.provider.JwtProvider;
import com.ex.backend.user.dto.PrincipalDetails;
import com.ex.backend.user.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final Logger logger = Logger.getLogger(JwtRequestFilter.class.getName());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 헤더에서 accessToken 추출
        String accessToken = request.getHeader("authorization");
        String requestURI = request.getRequestURI();
        logger.info(" requestURI: " + requestURI);

        if(requestURI.startsWith("/stomp/ws") || (accessToken == null && requestURI.equals("/auth/reissue"))) {
            filterChain.doFilter(request, response);
            return;

        } else if(accessToken == null && !requestURI.equals("/auth/reissue")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            logger.info("엑세스 토큰이 없고, 재발급 경로 요청이 아님");
            return;
        }

        // 토큰 만료 여부 확인
        try {
            jwtProvider.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            logger.info("AccessToken 만료됨");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰에서 유저정보 추출
        User user = User.builder()
                .userId(jwtProvider.getUserId(accessToken))
                .nickname(jwtProvider.getNickname(accessToken))
                .role(jwtProvider.getRole(accessToken))
                .build();

        PrincipalDetails customUserDetails = new PrincipalDetails(user);

        // 스프링에 유저정보 저장
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

}
