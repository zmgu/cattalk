package com.ex.backend.security.jwt.provider;

import com.ex.backend.security.jwt.constants.JwtConstants;
import com.ex.backend.security.jwt.props.JwtProps;
import com.ex.backend.user.dto.PrincipalDetails;
import com.ex.backend.user.dto.User;
import com.ex.backend.user.mapper.UserMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProps jwtProps;
    private final UserMapper userMapper;

    public String createAccessToken(String username, String role) {

        // accessToken 토큰 생성
        String accessToken = Jwts.builder()
                .signWith( getShaKey(), Jwts.SIG.HS512 )      // 서명에 사용할 키와 알고리즘 설정
                .header()                                                      // update (version : after 1.0)
                .add("typ", JwtConstants.TOKEN_TYPE)                   // 헤더 설정 (JWT)
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProps.getAccessTokenExpiredTime()))
                .claim("username", username)                              // 클레임 설정: 사용자 아이디
                .claim("role", role)                                      // 클레임 설정: 권한
                .compact();

        log.info("createAccessToken : " + accessToken);

        return accessToken;
    }

    public String createRefreshToken() {

        // refreshToken 토큰 생성
        String refreshToken = Jwts.builder()
                .signWith( getShaKey(), Jwts.SIG.HS512 )      // 서명에 사용할 키와 알고리즘 설정
                .header()                                                      // update (version : after 1.0)
                .add("typ", JwtConstants.TOKEN_TYPE)                   // 헤더 설정 (JWT)
                .and()
                .expiration(new Date(System.currentTimeMillis() + jwtProps.getRefreshTokenExpiredTime()))   // 토큰 만료 시간 설정 (10일)
                .compact();

        log.info("createRefreshToken : " + refreshToken);

        return refreshToken;
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String authHeader) {
        if(authHeader == null || authHeader.length() == 0 )
            return null;

        try {
            // jwt 추출 (Bearer + {jwt}) ➡ {jwt}
            String jwt = authHeader.replace(JwtConstants.TOKEN_PREFIX, "");

            // 🔐➡👩‍💼 JWT 파싱
            Jws<Claims> parsedToken = Jwts.parser()
                    .verifyWith(getShaKey())
                    .build()
                    .parseSignedClaims(jwt);

            log.info("parsedToken : " + parsedToken);

            // 인증된 사용자 아이디
            String username = parsedToken.getPayload().get("username").toString();
            log.info("username : " + username);

            // 인증된 사용자 권한
            Claims claims = parsedToken.getPayload();
            Object role = claims.get("role");
            log.info("role : " + role);

            // 토큰에 username 있는지 확인
            if( username == null || username.length() == 0 )
                return null;

            // 유저 정보 세팅
            User user = new User();
            user.setUsername(username);
            // OK: 권한도 바로 Users 객체에 담아보기
            user.setRole((String) role);

            // OK
            // CustomeUser 에 권한 담기
            List<SimpleGrantedAuthority> authorities = Collections.singletonList((new SimpleGrantedAuthority((String) role)));

            // 토큰 유효하면
            // name, email 도 담아주기
            try {
                User userInfo = userMapper.findByUsername(username);
                if( userInfo != null ) {
                    user.setName(userInfo.getName());
                    user.setEmail(userInfo.getEmail());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error("토큰 유효 -> DB 추가 정보 조회시 에러 발생...");
            }

            PrincipalDetails userDetails = new PrincipalDetails(user);

            // OK
            // new UsernamePasswordAuthenticationToken( 사용자정보객체, 비밀번호, 사용자의 권한(목록)  );
            return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        } catch (ExpiredJwtException exception) {
            log.warn("Request to parse expired JWT : {} failed : {}", authHeader, exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.warn("Request to parse unsupported JWT : {} failed : {}", authHeader, exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.warn("Request to parse invalid JWT : {} failed : {}", authHeader, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.warn("Request to parse empty or null JWT : {} failed : {}", authHeader, exception.getMessage());
        }

        return null;
    }

    public boolean validateToken(String jwt) {

        try {
            // JWT 파싱
            Jws<Claims> parsedToken = Jwts.parser()
                    .verifyWith(getShaKey())
                    .build()
                    .parseSignedClaims(jwt);

            log.info("##### 토큰 만료기간 #####");
            log.info("-> " + parsedToken.getPayload().getExpiration());

            Date exp = parsedToken.getPayload().getExpiration();

            // 만료시간과 현재시간 비교
            // 2023.12.01 vs 2023.12.14  --> 만료  : true  --->  false
            // 2023.12.30 vs 2023.12.14  --> 유효  : false --->  true
            return !exp.before(new Date());

        } catch (ExpiredJwtException exception) {
            log.error("Token Expired");                 // 토큰 만료
            return false;
        } catch (JwtException exception) {
            log.error("Token Tampered");                // 토큰 손상
            return false;
        } catch (NullPointerException exception) {
            log.error("Token is null");                 // 토큰 없음
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    // secretKey ➡ (HMAC-SHA algorithms) ➡ signingKey
    private SecretKey getShaKey() {
        return Keys.hmacShaKeyFor(jwtProps.getSecretKey().getBytes());
    }
}
