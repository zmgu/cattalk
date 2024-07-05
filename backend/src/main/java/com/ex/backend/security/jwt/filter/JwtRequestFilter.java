package com.ex.backend.security.jwt.filter;

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

        if(requestURI.startsWith("/stomp/ws")) {
            logger.info(" stomp 경로로 요청 옴 ");
            filterChain.doFilter(request, response);
            return;

        } else if (accessToken == null && requestURI.equals("/auth/reissue")) {
            // 엑세스 토큰이 없고, 재발급 경로 요청이었을 경우 다음 필터로 이동
            filterChain.doFilter(request, response);
            return;

        } else if(accessToken == null && !requestURI.equals("/auth/reissue")) {
            // 엑세스 토큰이 없고, 재발급 경로 요청이 아니었을 경우 상태 코드 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            logger.info("else if(accessToken == null && !requestURI.equals(\"/auth/reissue\"))");
            return;
        }

        // 엑세스 토큰 접두사 제거
        accessToken = accessToken.substring(7).trim();

        // 토큰 만료 여부 확인
        try {
            jwtProvider.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            logger.info("jwtProvider.isExpired(accessToken): accessToken 만료됨");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // username, role 값을 획득
        Long userId = jwtProvider.getUserId(accessToken);
        String nickname = jwtProvider.getNickname(accessToken);
        String role = jwtProvider.getRole(accessToken);

        User user = User.builder()
                .userId(userId)
                .nickname(nickname)
                .role(role)
                .build();

        PrincipalDetails customUserDetails = new PrincipalDetails(user);

        // 스프링에 유저정보 저장
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

}
