package com.project.platform.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * 景点信息
 */
@Data
public class ScenicInfo  {
    /**
     * id
     */
    private Integer id;
    /**
     * 景点分类id
     */
    private Integer categoryId;
    //   景点分类名称
    private String categoryType;
    /**
     * 景点名称
     */
    private String name;

    private BigDecimal originalPrice;
    /**
     * 收费价格
     */
    private BigDecimal price;
    /**
     * 描述
     */
    private String description;
    /**
     * 门票库存
     */
    private Integer stock;
    /**
     * 封面图
     */
    private String coverImage;
    /**
     * 详情图
     */
    private String detailImages;
    /**
     * 地址
     */
    private String address;
    /**
     * 开放时间
     */
    private String openingHours;
    /**
     * 状态
     */
    private String status;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    //评论数量
    private Integer countComment;

    //收藏数量
    private Integer countFavorite;

    //总评分
    private Double score;

    //是否已收藏（非数据库字段）
    private Boolean favorited;

}
