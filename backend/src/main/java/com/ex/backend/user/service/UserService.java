package com.ex.backend.user.service;

import com.ex.backend.user.entity.User;
import com.ex.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> selectUserList(Long userId) throws Exception {
        try {
            return userRepository.findAllByIdNotIn(userId);
        } catch (Exception e) {
            log.error("selectUserList 에러 : {}" + e.getMessage());
            throw new Exception("Failed to fetch user list", e);
        }
    }
}
