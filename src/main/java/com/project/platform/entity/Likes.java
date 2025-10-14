package com.project.platform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 点赞信息
 */
@Data
public class Likes {
    /**
     * id
     */
    private Integer id;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 类型
     */
    private String typeCode;
    /**
     * 关联Id
     */
    private Integer associationId;

    private Object associationObject;
    /**
     * 点赞时间
     */
    private LocalDateTime createTime;

}
