package com.ex.backend.user.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long userId;
    private String username;
    private String nickname;
    private String password;
    private String role;
    private String name;
    private String email;
    private Date regDate;
    private int enabled;
}
