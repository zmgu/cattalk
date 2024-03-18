package com.ex.backend.security.jwt.filter;

import com.ex.backend.security.jwt.provider.JwtProvider;
import com.ex.backend.user.dto.PrincipalDetails;
import com.ex.backend.user.dto.User;
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
import java.io.PrintWriter;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final Logger logger = Logger.getLogger(JwtRequestFilter.class.getName());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 헤더에서 accessToken 추출
        String accessToken = request.getHeader("accessToken");
        logger.info("accessToken :" + accessToken);

        String requestURI = request.getRequestURI();

        // 엑세스 토큰이 없고, 재발급 경로 요청이었을 경우는 다음 필터로 이동
        if (accessToken == null && requestURI.equals("/auth/reissue")) {
            filterChain.doFilter(request, response);
            return;
        } else if(accessToken == null && !requestURI.equals("/auth/reissue")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드 설정
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"No access token provided\"}");
            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtProvider.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtProvider.getCategory(accessToken);

        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // username, role 값을 획득
        String username = jwtProvider.getUsername(accessToken);
        String role = jwtProvider.getRole(accessToken);

        User user = new User();
        user.setUsername(username);
        user.setRole(role);
        PrincipalDetails customUserDetails = new PrincipalDetails(user);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

}
