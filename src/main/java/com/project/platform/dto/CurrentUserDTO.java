package com.project.platform.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CurrentUserDTO {
    private Integer id;
    private String type;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private String email;
    private BigDecimal balance;
    private String status;
    private String role;
    private LocalDateTime createTime;
}
