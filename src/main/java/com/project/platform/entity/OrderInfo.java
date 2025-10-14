package com.project.platform.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 订单信息
 */
@Data
public class OrderInfo {
    /**
     * id
     */
    private Integer id;
    /**
     * 订单号
     */
    private String orderNo;

    private List<OrderItem> orderItemList;
    /**
     * 用户id
     */
    private Integer userId;

    private User user;
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    /**
     * 支付状态
     */
    private String status;

    private Date visitDate;

    private String scenicName;

    private Integer quantity;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
