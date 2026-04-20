package com.project.platform.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * 用户信息（包含管理员）
 */
@Data
public class User  {
    /**
     * id
     */
    private Integer id;
    /**
     * 用户名（可修改，初始为手机号或邮箱）
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号（可用于登录，与邮箱至少填一个）
     */
    private String phone;
    /**
     * 邮箱（可用于登录，与手机号至少填一个）
     */
    private String email;
    /**
     * 头像
     */
    private String avatarUrl;
    /**
     * 余额
     */
    private BigDecimal balance;
    /**
     * 状态
     */
    private String status;
    /**
     * 角色: USER/ADMIN
     */
    private String role;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
