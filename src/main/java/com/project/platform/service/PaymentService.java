package com.project.platform.service;

import com.project.platform.dto.PaymentDTO;
import com.project.platform.dto.RechargeDTO;

import java.math.BigDecimal;

/**
 * 支付服务接口
 */
public interface PaymentService {
    
    /**
     * 余额支付
     * @param orderNo 订单号
     * @return 支付结果
     */
    String payByBalance(String orderNo);
    
    /**
     * 模拟第三方支付
     * @param paymentDTO 支付信息
     * @return 支付结果
     */
    String simulatePayment(PaymentDTO paymentDTO);
    
    /**
     * 用户充值（自己充值）
     * @param amount 充值金额
     * @param rechargeMethod 充值方式
     * @return 充值结果
     */
    String recharge(BigDecimal amount, String rechargeMethod);
    
    /**
     * 管理员为用户充值
     * @param rechargeDTO 充值信息
     * @return 充值结果
     */
    String adminRecharge(RechargeDTO rechargeDTO);
    
    /**
     * 订单退款
     * @param orderNo 订单号
     * @return 退款结果
     */
    String refund(String orderNo);
    
    /**
     * 取消订单
     * @param orderNo 订单号
     * @return 取消结果
     */
    String cancelOrder(String orderNo);
}
