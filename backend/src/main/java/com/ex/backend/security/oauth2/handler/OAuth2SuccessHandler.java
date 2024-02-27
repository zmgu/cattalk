package com.ex.backend.security.oauth2.handler;


import com.ex.backend.redis.RefreshTokenRepository;
import com.ex.backend.security.jwt.constants.JwtConstants;
import com.ex.backend.security.jwt.dto.RefreshToken;
import com.ex.backend.security.jwt.provider.JwtProvider;
import com.ex.backend.user.dto.PrincipalDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        PrincipalDetails user = (PrincipalDetails) authentication.getPrincipal();
        String username = user.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 💍 JWT 토큰 생성 요청
        String accessToken = jwtProvider.createAccessToken(username, role);
        String refreshToken = jwtProvider.createRefreshToken();

        log.info("accessToken" + accessToken);
        log.info("refreshToken" + refreshToken);

        refreshTokenRepository.save(new RefreshToken(refreshToken, username));

        // 💍 { Authorization : Bearer + {jwt} }
        response.addCookie(createCookie(JwtConstants.ACCESS_TOKEN_HEADER, accessToken, false));
        response.addCookie(createCookie(JwtConstants.REFRESH_TOKEN_HEADER, refreshToken, true));

        response.addHeader(JwtConstants.ACCESS_TOKEN_HEADER, JwtConstants.TOKEN_PREFIX + accessToken);
        response.setStatus(200);
        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String tokenHeader, String token, Boolean httpOnly) {

        Cookie cookie = new Cookie(tokenHeader, token);
        cookie.setMaxAge(600*60*60);

        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);

        return cookie;
    }
}
