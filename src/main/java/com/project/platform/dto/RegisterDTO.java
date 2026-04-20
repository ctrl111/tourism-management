package com.project.platform.dto;

import lombok.Data;

/**
 * 用户注册DTO
 */
@Data
public class RegisterDTO {
    /**
     * 手机号（与邮箱至少填一个）
     */
    private String phone;
    
    /**
     * 邮箱（与手机号至少填一个）
     */
    private String email;
    
    /**
     * 密码（必填）
     */
    private String password;
    
    /**
     * 确认密码（必填）
     */
    private String confirmPassword;
    
    /**
     * 头像URL（可选）
     */
    private String avatarUrl;
}
