package com.ex.backend.user.dto;

import lombok.Data;

import java.util.Date;

@Data
public class User {

    private Long id;
    private String username;
    private String nickname;
    private String password;
    private String role;
    private String name;
    private String email;
    private Date regDate;
    private Date updDate;
    private int enabled;
}
