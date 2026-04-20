package com.project.platform.dto;

import lombok.Data;

/**
 * 支付DTO
 */
@Data
public class PaymentDTO {
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 支付方式（模拟）: BALANCE-余额支付, ALIPAY-支付宝, WECHAT-微信
     */
    private String paymentMethod;
}
