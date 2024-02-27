package com.ex.backend.security.jwt.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("com.ex.backend")       // application.properties 의 하위 속성 경로 지정
public class JwtProps {

    private String secretKey;
    private int accessTokenExpiredTime;
    private int refreshTokenExpiredTime;
}