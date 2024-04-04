package com.ex.backend.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Friend {

    private Long friendId;
    private Long userId;
    private Long friendUserId;

}
