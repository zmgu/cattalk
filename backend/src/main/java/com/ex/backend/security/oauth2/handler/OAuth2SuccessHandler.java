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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Logger logger = Logger.getLogger(OAuth2SuccessHandler.class.getName());

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails user = (PrincipalDetails) authentication.getPrincipal();
        String username = user.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtProvider.createToken("access" ,username, role, 600000l);
        String refreshToken = jwtProvider.createToken("refresh" ,username, role, 86400000L);

        logger.info("accessToken" + accessToken);
        logger.info("refreshToken" + refreshToken);


        // Redis에 refreshToken 저장
        refreshTokenRepository.save(new RefreshToken(refreshToken, username));


        response.addCookie(createCookie(JwtConstants.REFRESH_TOKEN_HEADER, refreshToken));  // 쿠키에 refreshToken 저장
        response.addHeader(JwtConstants.ACCESS_TOKEN_HEADER, JwtConstants.TOKEN_PREFIX + accessToken);  // Header에 accessToken 저장
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String tokenHeader, String token) {

        Cookie cookie = new Cookie(tokenHeader, token);
        cookie.setMaxAge(600*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
