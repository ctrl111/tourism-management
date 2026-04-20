package com.project.platform.controller;

import com.project.platform.dto.PaymentDTO;
import com.project.platform.dto.RechargeDTO;
import com.project.platform.service.PaymentService;
import com.project.platform.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/payment")
@Tag(name = "支付管理", description = "支付相关操作API")
public class PaymentController {
    
    @Resource
    private PaymentService paymentService;
    
    /**
     * 统一支付接口
     * @param orderNo 订单号
     * @param paymentMethod 支付方式 (BALANCE/ALIPAY/WECHAT)
     * @return 支付结果
     */
    @PostMapping("/pay")
    @Operation(summary = "统一支付", description = "支持余额、支付宝、微信支付")
    public ResponseVO<String> pay(
            @RequestParam String orderNo,
            @RequestParam(defaultValue = "BALANCE") String paymentMethod) {
        String result;
        if ("BALANCE".equals(paymentMethod)) {
            result = paymentService.payByBalance(orderNo);
        } else {
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setOrderNo(orderNo);
            paymentDTO.setPaymentMethod(paymentMethod);
            result = paymentService.simulatePayment(paymentDTO);
        }
        return ResponseVO.ok(result);
    }
    
    /**
     * 余额支付
     * @param orderNo 订单号
     * @return 支付结果
     */
    @PostMapping("/payByBalance")
    @Operation(summary = "余额支付", description = "使用账户余额支付订单")
    public ResponseVO<String> payByBalance(@RequestParam String orderNo) {
        String result = paymentService.payByBalance(orderNo);
        return ResponseVO.ok(result);
    }
    
    /**
     * 模拟第三方支付
     * @param paymentDTO 支付信息
     * @return 支付结果
     */
    @PostMapping("/simulatePayment")
    @Operation(summary = "模拟支付", description = "模拟支付宝/微信支付")
    public ResponseVO<String> simulatePayment(@RequestBody PaymentDTO paymentDTO) {
        String result = paymentService.simulatePayment(paymentDTO);
        return ResponseVO.ok(result);
    }
    
    /**
     * 用户充值
     * @param amount 充值金额
     * @param rechargeMethod 充值方式
     * @return 充值结果
     */
    @PostMapping("/recharge")
    @Operation(summary = "用户充值", description = "用户为自己的账户充值")
    public ResponseVO<String> recharge(
            @RequestParam BigDecimal amount,
            @RequestParam(defaultValue = "ALIPAY") String rechargeMethod) {
        String result = paymentService.recharge(amount, rechargeMethod);
        return ResponseVO.ok(result);
    }
    
    /**
     * 管理员为用户充值
     * @param rechargeDTO 充值信息
     * @return 充值结果
     */
    @PostMapping("/adminRecharge")
    @Operation(summary = "管理员充值", description = "管理员为指定用户充值")
    public ResponseVO<String> adminRecharge(@RequestBody RechargeDTO rechargeDTO) {
        String result = paymentService.adminRecharge(rechargeDTO);
        return ResponseVO.ok(result);
    }
    
    /**
     * 订单退款
     * @param orderNo 订单号
     * @return 退款结果
     */
    @PostMapping("/refund")
    @Operation(summary = "订单退款", description = "退款到账户余额")
    public ResponseVO<String> refund(@RequestParam String orderNo) {
        String result = paymentService.refund(orderNo);
        return ResponseVO.ok(result);
    }
    
    /**
     * 取消订单
     * @param orderNo 订单号
     * @return 取消结果
     */
    @PostMapping("/cancelOrder")
    @Operation(summary = "取消订单", description = "取消未支付的订单")
    public ResponseVO<String> cancelOrder(@RequestParam String orderNo) {
        String result = paymentService.cancelOrder(orderNo);
        return ResponseVO.ok(result);
    }
}
