package com.ex.backend.user.service;

import com.ex.backend.user.entity.User;
import com.ex.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger logger = Logger.getLogger(UserService.class.getName());
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public User findByUsername(String username) throws Exception {
        return userRepository.findByUsername(username).orElseThrow(() -> new Exception("User not found"));
    }

    public void login(User user, HttpServletRequest request) throws Exception {
        String username = user.getUsername();
        String password = user.getPassword();
        log.info("username : " + username);
        log.info("password : " + password);

        // AuthenticationManager
        // 아이디, 패스워드 인증 토큰 생성
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

        // 토큰에 요청정보 등록
        token.setDetails(new WebAuthenticationDetails(request));

        // 토큰을 이용하여 인증 요청 - 로그인
        Authentication authentication = authenticationManager.authenticate(token);

        log.info("인증 여부 : " +  authentication.isAuthenticated());

        User authUser = (User) authentication.getPrincipal();
        log.info("인증된 사용자 아이디 : " + authUser.getUsername());

        // 시큐리티 컨텍스트에 인증된 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public int insert(User user) throws Exception {
        // 비밀번호 암호화
        String password = user.getPassword();
        String enPassword = passwordEncoder.encode(password);
        user.setPassword(enPassword);
        user.setRole("ROLE_USER");

        // 회원 등록
        userRepository.save(user);

        return 1;
    }

    public List<User> selectUserList(Long userId) throws Exception {
        try {
            return userRepository.findAllByIdNotIn(userId);
        } catch (Exception e) {
            logger.severe("selectUserList 에러 : " + e.getMessage());
            throw new Exception("Failed to fetch user list", e);
        }
    }
}
