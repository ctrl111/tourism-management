package com.project.platform.entity;

import lombok.Data;

import java.time.LocalDateTime;
/**
 * 景点分类信息
 */
@Data
public class ScenicCategory  {
    /**
     * id
     */
    private Integer id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
