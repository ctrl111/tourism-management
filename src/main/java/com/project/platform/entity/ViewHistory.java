package com.project.platform.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 浏览历史
 */
@Data
public class ViewHistory  {
    /**
     * id
     */
    private Integer id;

    private String typeCode;
    /**
     * 用户id
     */
    private Integer userId;

    private  User user;
    /**
     * 关联Id
     */
    private Integer associationId;

    private Object associationObject;

    /**
     * 浏览时间
     */
    private LocalDateTime viewTime;

}
