package com.project.platform.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论记录
 */
@Data
public class CommentInfo {
    /**
     * id
     */
    private Integer id;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 评论类型
     */
    private String typeCode;
    /**
     * 关联Id
     */
    private Integer associationId;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 父评论ID (用于回复评论)
     */
    private Integer parentId;
    /**
     * 评论时间
     */
    private LocalDateTime createTime;

    private User user;

    private List<CommentInfo> childList;

}
