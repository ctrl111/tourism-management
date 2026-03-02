package com.project.platform.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 订单信息（简化版）
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
    /**
     * 用户id
     */
    private Integer userId;

    private User user;
    /**
     * 景点ID
     */
    private Integer scenicId;
    /**
     * 景点名称
     */
    private String scenicName;
    /**
     * 票数
     */
    private Integer quantity;
    /**
     * 单价
     */
    private BigDecimal unitPrice;
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    /**
     * 支付状态
     */
    private String status;
    /**
     * 游玩日期
     */
    private Date visitDate;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
