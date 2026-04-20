package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.dto.PaymentDTO;
import com.project.platform.dto.RechargeDTO;
import com.project.platform.entity.Notice;
import com.project.platform.entity.OrderInfo;
import com.project.platform.entity.ScenicInfo;
import com.project.platform.entity.User;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.NoticeMapper;
import com.project.platform.mapper.OrderInfoMapper;
import com.project.platform.mapper.ScenicInfoMapper;
import com.project.platform.mapper.UserMapper;
import com.project.platform.service.PaymentService;
import com.project.platform.utils.CurrentUserThreadLocal;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付服务实现类
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Resource
    private OrderInfoMapper orderInfoMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private ScenicInfoMapper scenicInfoMapper;
    
    @Resource
    private NoticeMapper noticeMapper;
    
    /**
     * 余额支付
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String payByBalance(String orderNo) {
        // 获取当前用户
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        
        // 查询订单
        OrderInfo order = orderInfoMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new CustomException("Заказ не найден");
        }
        
        // 验证订单所属
        if (!order.getUserId().equals(currentUser.getId())) {
            throw new CustomException("У вас нет прав на эту операцию");
        }
        
        // 检查订单状态
        if ("PAID".equals(order.getStatus())) {
            throw new CustomException("Заказ уже оплачен");
        }
        
        if ("CANCELLED".equals(order.getStatus())) {
            throw new CustomException("Заказ отменён");
        }
        
        // 查询用户余额
        User user = userMapper.selectById(currentUser.getId());
        if (user.getBalance().compareTo(order.getTotalAmount()) < 0) {
            throw new CustomException("Недостаточно средств. Баланс: " + user.getBalance() + " ₽, требуется: " + order.getTotalAmount() + " ₽");
        }
        
        // 检查景点库存
        ScenicInfo scenic = scenicInfoMapper.selectById(order.getScenicId());
        if (scenic != null && scenic.getStock() != null && scenic.getStock() < order.getQuantity()) {
            throw new CustomException("Недостаточно билетов. Доступно: " + scenic.getStock());
        }
        
        // 扣除余额
        user.setBalance(user.getBalance().subtract(order.getTotalAmount()));
        userMapper.updateById(user);
        
        // 减少库存
        if (scenic != null && scenic.getStock() != null) {
            scenic.setStock(scenic.getStock() - order.getQuantity());
            scenicInfoMapper.updateById(scenic);
        }
        
        // 更新订单状态
        order.setStatus("PAID");
        orderInfoMapper.updateById(order);
        
        // 发送通知
        sendNotice(currentUser.getId(), "Оплата выполнена", 
            "Заказ " + orderNo + " успешно оплачен. Сумма: " + order.getTotalAmount() + " ₽");
        
        return "Оплата выполнена успешно";
    }
    
    /**
     * 模拟第三方支付（支付宝/微信）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String simulatePayment(PaymentDTO paymentDTO) {
        // 获取当前用户
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        
        // 查询订单
        OrderInfo order = orderInfoMapper.selectByOrderNo(paymentDTO.getOrderNo());
        if (order == null) {
            throw new CustomException("Заказ не найден");
        }
        
        // 验证订单所属
        if (!order.getUserId().equals(currentUser.getId())) {
            throw new CustomException("У вас нет прав на эту операцию");
        }
        
        // 检查订单状态
        if ("PAID".equals(order.getStatus())) {
            throw new CustomException("Заказ уже оплачен");
        }
        
        if ("CANCELLED".equals(order.getStatus())) {
            throw new CustomException("Заказ отменён");
        }
        
        // 检查景点库存
        ScenicInfo scenic = scenicInfoMapper.selectById(order.getScenicId());
        if (scenic != null && scenic.getStock() != null && scenic.getStock() < order.getQuantity()) {
            throw new CustomException("Недостаточно билетов. Доступно: " + scenic.getStock());
        }
        
        // 减少库存
        if (scenic != null && scenic.getStock() != null) {
            scenic.setStock(scenic.getStock() - order.getQuantity());
            scenicInfoMapper.updateById(scenic);
        }
        
        // 更新订单状态
        order.setStatus("PAID");
        orderInfoMapper.updateById(order);
        
        // 发送通知
        String paymentMethodName = getPaymentMethodName(paymentDTO.getPaymentMethod());
        sendNotice(currentUser.getId(), "Оплата выполнена", 
            "Заказ " + paymentDTO.getOrderNo() + " оплачен через " + paymentMethodName + ". Сумма: " + order.getTotalAmount() + " ₽");
        
        return "Оплата выполнена успешно";
    }
    
    /**
     * 用户充值
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String recharge(BigDecimal amount, String rechargeMethod) {
        // 获取当前用户
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        
        // 验证充值金额
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException("Сумма пополнения должна быть больше нуля");
        }
        
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            throw new CustomException("Максимальная сумма пополнения: 10 000 ₽");
        }
        
        // 查询用户
        User user = userMapper.selectById(currentUser.getId());
        
        // 增加余额
        user.setBalance(user.getBalance().add(amount));
        userMapper.updateById(user);
        
        // 发送通知
        String methodName = getPaymentMethodName(rechargeMethod);
        sendNotice(currentUser.getId(), "Пополнение выполнено", 
            "Счёт пополнен через " + methodName + " на " + amount + " ₽. Баланс: " + user.getBalance() + " ₽");
        
        return "Пополнение выполнено. Баланс: " + user.getBalance() + " ₽";
    }
    
    /**
     * 管理员为用户充值
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String adminRecharge(RechargeDTO rechargeDTO) {
        // 获取当前用户（管理员）
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (!"ADMIN".equals(currentUser.getType())) {
            throw new CustomException("Недостаточно прав");
        }
        
        // 验证充值金额
        if (rechargeDTO.getAmount() == null || rechargeDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException("Сумма пополнения должна быть больше нуля");
        }
        
        // 查询目标用户
        User user = userMapper.selectById(rechargeDTO.getUserId());
        if (user == null) {
            throw new CustomException("Пользователь не найден");
        }
        
        // 增加余额
        user.setBalance(user.getBalance().add(rechargeDTO.getAmount()));
        userMapper.updateById(user);
        
        // 发送通知
        sendNotice(rechargeDTO.getUserId(), "Пополнение счёта", 
            "Администратор пополнил ваш счёт на " + rechargeDTO.getAmount() + " ₽. Баланс: " + user.getBalance() + " ₽");
        
        return "Пополнение выполнено успешно";
    }
    
    /**
     * 订单退款
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String refund(String orderNo) {
        // 获取当前用户
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        
        // 查询订单
        OrderInfo order = orderInfoMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new CustomException("Заказ не найден");
        }
        
        // 验证订单所属（管理员可以退款任何订单）
        if (!"ADMIN".equals(currentUser.getType()) && !order.getUserId().equals(currentUser.getId())) {
            throw new CustomException("У вас нет прав на эту операцию");
        }
        
        // 检查订单状态
        if (!"PAID".equals(order.getStatus())) {
            throw new CustomException("Возврат возможен только для оплаченных заказов");
        }
        
        if ("REFUNDED".equals(order.getStatus())) {
            throw new CustomException("Возврат уже выполнен");
        }
        
        // 查询用户
        User user = userMapper.selectById(order.getUserId());
        
        // 退款到余额
        user.setBalance(user.getBalance().add(order.getTotalAmount()));
        userMapper.updateById(user);
        
        // 恢复库存
        ScenicInfo scenic = scenicInfoMapper.selectById(order.getScenicId());
        if (scenic != null && scenic.getStock() != null) {
            scenic.setStock(scenic.getStock() + order.getQuantity());
            scenicInfoMapper.updateById(scenic);
        }
        
        // 更新订单状态
        order.setStatus("REFUNDED");
        orderInfoMapper.updateById(order);
        
        // 发送通知
        sendNotice(order.getUserId(), "Возврат выполнен", 
            "Возврат по заказу " + orderNo + " выполнен. Сумма " + order.getTotalAmount() + " ₽ возвращена на ваш счёт");
        
        return "Возврат выполнен успешно";
    }
    
    /**
     * 取消订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String cancelOrder(String orderNo) {
        // 获取当前用户
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        
        // 查询订单
        OrderInfo order = orderInfoMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new CustomException("Заказ не найден");
        }
        
        // 验证订单所属
        if (!order.getUserId().equals(currentUser.getId())) {
            throw new CustomException("У вас нет прав на эту операцию");
        }
        
        // 检查订单状态
        if ("PAID".equals(order.getStatus())) {
            throw new CustomException("Оплаченный заказ нельзя отменить. Оформите возврат");
        }
        
        if ("CANCELLED".equals(order.getStatus())) {
            throw new CustomException("Заказ уже отменён");
        }
        
        if ("REFUNDED".equals(order.getStatus())) {
            throw new CustomException("Возвращённый заказ нельзя отменить");
        }
        
        // 更新订单状态
        order.setStatus("CANCELLED");
        orderInfoMapper.updateById(order);
        
        // 发送通知
        sendNotice(currentUser.getId(), "Заказ отменён", 
            "Заказ " + orderNo + " отменён");
        
        return "Заказ отменён";
    }
    
    /**
     * 发送通知
     */
    private void sendNotice(Integer userId, String title, String content) {
        Notice notice = new Notice();
        notice.setUserId(userId);
        notice.setTypeCode("Оплата");
        notice.setTitle(title);
        notice.setContent(content);
        notice.setIsRead("NO");
        noticeMapper.insert(notice);
    }
    
    /**
     * 获取支付方式名称
     */
    private String getPaymentMethodName(String method) {
        if (method == null) {
            return "неизвестный способ";
        }
        switch (method.toUpperCase()) {
            case "BALANCE":
                return "баланс";
            case "ALIPAY":
                return "Alipay";
            case "WECHAT":
                return "WeChat";
            case "BANK":
                return "банковская карта";
            default:
                return method;
        }
    }
}
