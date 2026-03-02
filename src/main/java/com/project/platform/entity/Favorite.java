package com.project.platform.entity;

import lombok.Data;

import java.time.LocalDateTime;
/**
 * 收藏信息
 */
@Data
public class Favorite {
    /**
     * id
     */
    private Integer id;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 收藏类型
     */
    private String typeCode;
    /**
     * 关联Id
     */
    private Integer associationId;
    /**
     * 收藏时间
     */
    private LocalDateTime createTime;

}
