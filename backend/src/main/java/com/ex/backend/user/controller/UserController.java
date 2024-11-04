package com.ex.backend.user.controller;

import com.ex.backend.user.dto.PrincipalDetails;
import com.ex.backend.user.entity.User;
import com.ex.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 사용자 정보 조회
     * @param principalDetails
     */
    @GetMapping("/info")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        return user != null ? new ResponseEntity<>(user, HttpStatus.OK) : new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping
    public ResponseEntity<List<User>> selectUserList(@RequestParam(name = "userId") Long userId) {
        try {
            List<User> userList = userService.selectUserList(userId);
            return new ResponseEntity<>(userList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
