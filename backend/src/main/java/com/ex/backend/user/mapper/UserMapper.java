package com.ex.backend.user.mapper;

import com.ex.backend.user.dto.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    User findByUsername(String username) throws Exception;

    List<User> selectUserList(Long userId) throws Exception;

    void save(User user) throws Exception;

    void oauthSave(User user) throws Exception;

    void update(User user) throws Exception;

    int insert(User user) throws Exception;
}
