package com.project.platform.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点评论信息
 */
@Data
public class ScenicComment  {
    /**
     * id
     */
    private Integer id;
    /**
     * 用户id
     */
    private Integer userId;

    private User user;

    /**
     * 用户名
     */
    private String username;
    /**
     * 景点id
     */
    private Integer scenicId;

    private ScenicInfo scenicInfo;

    /**
     * 景点名
     */
    private String scenicName;
    /**
     * 内容
     */
    private String content;
    /**
     * 评分
     */
    private Double score;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private List<ScenicComment> childList;


}
