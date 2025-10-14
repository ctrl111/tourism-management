package com.project.platform.entity;

import lombok.Data;

import java.time.LocalDateTime;
/**
 * 轮播图信息
 */
@Data
public class Banner  {
    /**
     * id
     */
    private Integer id;
    /**
     * 图片路径
     */
    private String imageUrl;
    /**
     * 跳转id
     */
    private Integer linkId;
    /**
     * 标题
     */
    private String title;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态
     */
    private String status;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
