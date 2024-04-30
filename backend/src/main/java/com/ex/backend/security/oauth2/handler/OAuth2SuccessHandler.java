package com.ex.backend.security.oauth2.handler;


import com.ex.backend.redis.RefreshTokenRedis;
import com.ex.backend.security.jwt.constants.JwtConstants;
import com.ex.backend.security.jwt.cookie.CookieUtil;
import com.ex.backend.security.jwt.dto.RefreshToken;
import com.ex.backend.security.jwt.provider.JwtProvider;
import com.ex.backend.user.dto.PrincipalDetails;
import jakarta.servlet.ServletException;
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
    private final RefreshTokenRedis refreshTokenRedis;
    private final CookieUtil cookieUtil;
    private final Logger logger = Logger.getLogger(OAuth2SuccessHandler.class.getName());

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails user = (PrincipalDetails) authentication.getPrincipal();

        Long userId = user.getUserId();
        logger.info("userId : " + userId);
        String nickname = user.getNickname();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String refreshToken = jwtProvider.createToken(JwtConstants.REFRESH_TOKEN, userId, nickname, role);

        logger.info("refreshToken: " + refreshToken);

        // Redis에 refreshToken 저장
        refreshTokenRedis.createRefreshToken(new RefreshToken(refreshToken, String.valueOf(userId)));

        response.addCookie(cookieUtil.createCookie(JwtConstants.REFRESH_TOKEN, refreshToken));
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect("http://localhost:3000/oauth2");
    }

}
