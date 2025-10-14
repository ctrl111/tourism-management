package com.project.platform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 游记分享信息
 */
@Data
public class TravelNote  {
    /**
     * id
     */
    private Integer id;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 用户
     */
    private User user;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;

    /**
     * 封面
     */
    private String cover;
    /**
     * 行程时间
     */
    private String travelTime;
    /**
     * 天数
     */
    private String days;

    private Boolean Liked;

    private int viewCount;

    private int commentsCount;

    private int  likesCount;

    private int  favoriteCount;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
