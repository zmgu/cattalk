package com.ex.backend.user.service;

import com.ex.backend.security.oauth2.dto.GoogleResponse;
import com.ex.backend.security.oauth2.dto.KakaoResponse;
import com.ex.backend.security.oauth2.dto.NaverResponse;
import com.ex.backend.security.oauth2.dto.OAuth2Response;
import com.ex.backend.user.dto.PrincipalDetails;
import com.ex.backend.user.entity.User;
import com.ex.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        switch (registrationId) {
            case "naver" -> oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
            case "google" -> oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            case "kakao" -> oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        return new PrincipalDetails(findByUsername(oAuth2Response, username));
    }

    public User findByUsername(OAuth2Response oAuth2Response, String username) {

        Optional<User> userInfoOptional = userRepository.findByUsername(username);
        User userInfo;

        if (userInfoOptional.isEmpty()) {
            User newUser = User.builder()
                    .username(username)
                    .email(oAuth2Response.getEmail())
                    .nickname(oAuth2Response.getName()) // 초기 닉네임은 사용자의 이름으로 설정
                    .name(oAuth2Response.getName())
                    .role("ROLE_USER")
                    .build();

            userInfo = userRepository.save(newUser);
        } else {
            userInfo = userInfoOptional.get();
            userInfo.setEmail(oAuth2Response.getEmail());
            userRepository.save(userInfo);
        }

        return userInfo;
    }
}