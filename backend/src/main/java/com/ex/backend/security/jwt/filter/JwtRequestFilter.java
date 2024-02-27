package com.ex.backend.security.jwt.filter;

import com.ex.backend.security.jwt.constants.JwtConstants;
import com.ex.backend.security.jwt.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    /**
     *  jwt 요청 필터
     *  - request > headers > Authorization (💍JWT)
     *  - JWT 토큰 유효성 검사
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 헤더에서 accessToken 가져옴
        String header = request.getHeader(JwtConstants.ACCESS_TOKEN_HEADER);
        log.info("AccessToken : " + header);

        // accessToken 토큰이 없으면 다음 필터로 이동
        if( header == null || header.length() == 0 || !header.startsWith(JwtConstants.TOKEN_PREFIX) ) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer + {accessToken} ➡ "Bearer " 제거
        String accessToken = header.replace(JwtConstants.TOKEN_PREFIX, "");

        // 토큰 해석
        Authentication authenticaion = jwtProvider.getAuthentication(accessToken);

        // 토큰 유효성 검사
        if( jwtProvider.validateToken(accessToken) ) {
            log.info("유효한 accessToken 입니다.");

            // 로그인
            SecurityContextHolder.getContext().setAuthentication(authenticaion);

        } else if( !jwtProvider.validateToken(accessToken) ){
            log.info("만료된 accessToken 입니다. ");
            log.info("refreshToken이 존재하는지 검색중...");

            log.info("재발급 중입니다...");

            // accessToken 재발급 로직

        }

        // 다음 필터
        filterChain.doFilter(request, response);
    }

}
