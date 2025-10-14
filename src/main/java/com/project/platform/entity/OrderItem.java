package com.project.platform.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * 订单详情
 */
@Data
public class OrderItem  {
    /**
     * id
     */
    private Integer id;
    /**
     * 订单id
     */
    private Integer orderId;
    /**
     * 景点id
     */
    private Integer attractionId;

    private String scenicName;
    /**
     * 购买数量
     */
    private Integer quantity;
    /**
     * price
     */
    private BigDecimal price;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
