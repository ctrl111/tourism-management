package com.project.platform.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 充值DTO
 */
@Data
public class RechargeDTO {
    /**
     * 用户ID（管理员充值时使用）
     */
    private Integer userId;
    
    /**
     * 充值金额
     */
    private BigDecimal amount;
    
    /**
     * 充值方式（模拟）: ALIPAY-支付宝, WECHAT-微信, BANK-银行卡
     */
    private String rechargeMethod;
}
