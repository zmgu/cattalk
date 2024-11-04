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

    public List<User> selectUserList(Long userId) {
        return userRepository.findAllByIdNotIn(userId);
    }
}
