package com.project.platform.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统通知
 */
@Data
public class Notice  {
    /**
     * id
     */
    private Integer id;

    /**
     * userId
     */
    private Integer userId;

    /**
     * userIds
     */
    private List<Integer> userIds;

    private User user;
    /**
     *通知类型
     */
    private String typeCode;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 是否已读
     */
    private String isRead;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
