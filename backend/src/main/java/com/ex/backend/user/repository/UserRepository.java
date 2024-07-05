package com.ex.backend.user.repository;

import com.ex.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.userId != :userId ORDER BY u.nickname ASC")
    List<User> findAllByIdNotIn(@Param("userId") Long userId);
}
