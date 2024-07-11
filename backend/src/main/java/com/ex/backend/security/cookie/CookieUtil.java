package com.ex.backend.security.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public Cookie createCookie(String tokenHeader, String token) {

        Cookie cookie = new Cookie(tokenHeader, token);
        cookie.setMaxAge(60*60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    public Cookie deleteCookie(String tokenHeader) {

        Cookie cookie = new Cookie(tokenHeader, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        return cookie;
    }
}
