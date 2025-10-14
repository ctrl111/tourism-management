package com.project.platform.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * 用户信息
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
     * 状态
     */
    private String status;

    private BigDecimal  balance;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
