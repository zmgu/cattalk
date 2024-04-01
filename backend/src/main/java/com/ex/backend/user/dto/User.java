package com.ex.backend.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class User {

    private final Long id;
    private String username;
    private String nickname;
    private String password;
    private String role;
    private String name;
    private String email;
    private final Date regDate;
    private int enabled;
}
