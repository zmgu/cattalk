package com.ex.backend.user.controller;

import com.ex.backend.user.dto.PrincipalDetails;
import com.ex.backend.user.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * 사용자 정보 조회
     * @param principalDetails
     * @return
     */
    @GetMapping("/info")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        User user = principalDetails.getUser();

        // 인증된 사용자 정보
        if( user != null )
            return new ResponseEntity<>(user, HttpStatus.OK);

        // 인증 되지 않음
        return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
}
