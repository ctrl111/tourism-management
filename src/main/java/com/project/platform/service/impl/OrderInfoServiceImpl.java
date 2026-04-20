package com.project.platform.service.impl;

import com.project.platform.dto.BuyTicketDTO;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.*;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.*;
import com.project.platform.service.OrderInfoService;
import com.project.platform.service.UserService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单信息（简化版）
 */
@Service
public class OrderInfoServiceImpl implements OrderInfoService {
    @Resource
    private OrderInfoMapper orderMapper;

    @Resource
    private UserService userService;

    @Resource
    private ScenicInfoMapper scenicInfoMapper;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public PageVO<OrderInfo> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        CurrentUserDTO userDTO = CurrentUserThreadLocal.getCurrentUser();
        if (userDTO.getType().equals("USER")){
            query.put("userId", userDTO.getId());
        }
        PageVO<OrderInfo> page = new PageVO();
        List<OrderInfo> list = orderMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        
        // 填充用户信息
        for (OrderInfo orderInfo : list) {
            orderInfo.setUser(userMapper.selectById(orderInfo.getUserId()));
        }
        
        page.setList(list);
        page.setTotal(orderMapper.queryCount(query));
        return page;
    }

    @Override
    public OrderInfo selectById(Integer id) {
        OrderInfo order = orderMapper.selectById(id);
        if (order != null) {
            order.setUser(userMapper.selectById(order.getUserId()));
        }
        return order;
    }

    @Override
    public List<OrderInfo> list() {
        return orderMapper.list();
    }

    @Override
    public void insert(OrderInfo entity) {
        check(entity);
        orderMapper.insert(entity);
    }

    @Override
    public void updateById(OrderInfo entity) {
        check(entity);
        orderMapper.updateById(entity);
    }

    private void check(OrderInfo entity) {
        // 验证逻辑
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        orderMapper.removeByIds(ids);
    }

    /**
     * 购买门票（简化版）- 真实支付流程（保留备用）
     * @param scenicId 景点ID
     * @param quantity 数量
     * @param visitDate 游玩日期
     * @return 订单号
     */
    @Transactional(rollbackFor = Exception.class)
    public String buyTicketWithPayment(Integer scenicId, Integer quantity, String visitDate) {
        // 获取当前用户
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        
        // 验证景点有效性
        ScenicInfo scenic = scenicInfoMapper.selectById(scenicId);
        if (scenic == null) {
            throw new CustomException("Достопримечательность не найдена");
        }
        
        // 计算总金额
        BigDecimal unitPrice = scenic.getPrice();
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        
        // 验证余额
        User user = userMapper.selectById(currentUser.getId());
        if (user.getBalance().compareTo(totalAmount) < 0) {
            throw new CustomException("Недостаточно средств");
        }
        
        // 创建订单
        OrderInfo order = new OrderInfo();
        order.setUserId(currentUser.getId());
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setScenicId(scenicId);
        order.setScenicName(scenic.getName());
        order.setQuantity(quantity);
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(totalAmount);
        order.setStatus("PAID"); // 已支付状态
        
        // 解析游玩日期
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            order.setVisitDate(formatter.parse(visitDate));
        } catch (ParseException e) {
            throw new CustomException("Неверный формат даты");
        }
        
        // 扣除余额
        userService.consumption(currentUser.getId(), totalAmount);
        
        // 保存订单
        orderMapper.insert(order);
        
        // 发送通知
        Notice notice = new Notice();
        notice.setTypeCode("Заказ");
        notice.setUserId(currentUser.getId());
        notice.setContent("Заказ успешно оплачен. Номер заказа: " + order.getOrderNo());
        notice.setIsRead("UNREAD");
        notice.setTitle("Оплата выполнена");
        noticeMapper.insert(notice);
        
        return order.getOrderNo();
    }

    /**
     * 购买门票（使用DTO）- 创建待支付订单
     * @param dto 购票信息
     * @return 订单号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String buyTickets(BuyTicketDTO dto) {
        // 获取当前用户
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        
        // 验证景点有效性
        ScenicInfo scenic = scenicInfoMapper.selectById(dto.getId());
        if (scenic == null) {
            throw new CustomException("Достопримечательность не найдена");
        }
        
        // 检查景点状态
        if ("INACTIVE".equals(scenic.getStatus())) {
            throw new CustomException("Бронирование временно недоступно");
        }
        
        // 检查库存（预检查，实际扣减在支付时进行）
        if (scenic.getStock() != null && scenic.getStock() < dto.getNumber()) {
            throw new CustomException("Недостаточно билетов. Доступно: " + scenic.getStock());
        }
        
        // 验证购买数量
        if (dto.getNumber() == null || dto.getNumber() <= 0) {
            throw new CustomException("Количество должно быть больше нуля");
        }
        
        if (dto.getNumber() > 10) {
            throw new CustomException("Максимум 10 билетов за раз");
        }
        
        // 计算总金额
        BigDecimal unitPrice = scenic.getPrice();
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(dto.getNumber()));
        
        // 创建订单（待支付状态）
        OrderInfo order = new OrderInfo();
        order.setUserId(currentUser.getId());
        order.setOrderNo(generateOrderNo());
        order.setScenicId(dto.getId());
        order.setScenicName(scenic.getName());
        order.setQuantity(dto.getNumber());
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING"); // 待支付状态
        
        // 解析游玩日期
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            order.setVisitDate(formatter.parse(dto.getVisitDate()));
        } catch (ParseException e) {
            throw new CustomException("Неверный формат даты. Используйте формат yyyy-MM-dd");
        }
        
        // 保存订单
        orderMapper.insert(order);
        
        // 发送通知
        Notice notice = new Notice();
        notice.setTypeCode("Заказ");
        notice.setUserId(currentUser.getId());
        notice.setContent("Заказ создан. Номер: " + order.getOrderNo() + ". Сумма: " + totalAmount + " ₽. Оплатите в течение 30 минут");
        notice.setIsRead("NO");
        notice.setTitle("Заказ создан");
        noticeMapper.insert(notice);
        
        return order.getOrderNo();
    }
    
    /**
     * 生成订单号
     * 格式：yyyyMMddHHmmss + 6位随机数
     */
    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new java.util.Date());
        int random = (int) ((Math.random() * 9 + 1) * 100000);
        return timestamp + random;
    }
}
