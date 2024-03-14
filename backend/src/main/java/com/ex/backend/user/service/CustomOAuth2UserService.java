package com.ex.backend.user.service;

import com.ex.backend.security.oauth2.dto.GoogleResponse;
import com.ex.backend.security.oauth2.dto.NaverResponse;
import com.ex.backend.security.oauth2.dto.OAuth2Response;
import com.ex.backend.security.oauth2.handler.OAuth2SuccessHandler;
import com.ex.backend.user.dto.PrincipalDetails;
import com.ex.backend.user.dto.User;
import com.ex.backend.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserMapper userMapper;
    private final Logger logger = Logger.getLogger(CustomOAuth2UserService.class.getName());

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        PrincipalDetails principalDetails = null;

        try {
            User existData = userMapper.findByUsername(username);

            if (existData == null) {

                User user = new User();
                user.setUsername(username);
                user.setEmail(oAuth2Response.getEmail());
                user.setName(oAuth2Response.getName());
                user.setRole("ROLE_USER");
                logger.info("user: " + user);
                userMapper.oauthSave(user);

                principalDetails = new PrincipalDetails(user);
            }
            else {

                existData.setEmail(oAuth2Response.getEmail());
                existData.setName(oAuth2Response.getName());

                userMapper.update(existData);

                principalDetails = new PrincipalDetails(existData);
            }
        } catch (Exception e) {

        }
        logger.info("username: " + username);

        return principalDetails;
    }
}