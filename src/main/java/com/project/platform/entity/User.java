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
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 密码
     */
    private String password;
    /**
     *  电话
     */
    private String phone;
    /**
     * 邮箱
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
