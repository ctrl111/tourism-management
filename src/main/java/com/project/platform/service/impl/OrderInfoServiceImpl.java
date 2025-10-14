package com.project.platform.service.impl;

//import com.project.platform.dto.BuyTicketDTO;
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
 * 订单信息
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
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ScenicInfoMapper scenicMapper;
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
        for (OrderInfo orderInfo : list) {
            List<OrderItem> items = orderItemMapper.selectByOrderId(orderInfo.getId());
            orderInfo.setOrderItemList(items);
            String scenicName = "";
            int quantity=0;
            for (OrderItem item : items) {
                quantity += item.getQuantity();
                ScenicInfo scenicInfo = scenicMapper.selectById(item.getAttractionId());
                if (scenicName.equals("")){
                    scenicName = scenicInfo.getName();
                    continue;
                }else {
                    scenicName =  scenicName+","+scenicInfo.getName();
                }
            }
            orderInfo.setScenicName(scenicName);
            orderInfo.setQuantity(quantity);
            orderInfo.setUser(userMapper.selectById(orderInfo.getUserId()));
        }
        page.setList(list);
        page.setTotal(orderMapper.queryCount(query));
        return page;
    }

    @Override
    public OrderInfo selectById(Integer id) {
        OrderInfo order = orderMapper.selectById(id);
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

    }

    @Override
    public void removeByIds(List<Integer> ids) {
        orderMapper.removeByIds(ids);
    }

//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public String buyTickets(BuyTicketDTO dto) {
//        // 获取当前用户
//        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
//        // 验证景点有效性
//        ScenicInfo scenic = scenicInfoMapper.selectById(dto.getId());
//        if (scenic == null) {
//            throw new CustomException("景点不存在");
//
//        }
//        // 创建订单基础信息
//        OrderInfo order = buildBaseOrder(currentUser, dto, scenic);
//        // 处理支付逻辑
//        processPayment(currentUser.getId(), order.getTotalAmount());
//
//        // 保存订单相关记录
//        saveOrderRecords(order, dto, scenic);
//        return "订单创建成功";
//    }
//
//    private OrderInfo buildBaseOrder(CurrentUserDTO user, BuyTicketDTO dto, ScenicInfo scenic) {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        OrderInfo order = new OrderInfo();
//        order.setUserId(user.getId());
//        order.setOrderNo(UUID.randomUUID().toString().replace("-",""));
//        order.setStatus("未支付");
//        try {
//            order.setVisitDate(formatter.parse(dto.getVisitDate()));
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//        order.setTotalAmount(calculateTotal(scenic.getPrice(), dto.getNumber()));
//        Notice notice = new Notice();
//        notice.setTypeCode("订单");
//        notice.setUserId(user.getId());
//        notice.setContent("您的订单已创建，订单号为：" + order.getOrderNo());
//        notice.setIsRead("未读");
//        notice.setTitle("创建订单");
//        noticeMapper.insert(notice);
//        return order;
//    }
//
//    private BigDecimal calculateTotal(BigDecimal price, Integer quantity) {
//        return price.multiply(BigDecimal.valueOf(quantity));
//    }
//
//    private void processPayment(Integer userId, BigDecimal amount) {
//        User user = userMapper.selectById(userId);
//        BigDecimal userBalance = user.getBalance();
//        if (userBalance.compareTo(amount) < 0) {
//            throw new CustomException("余额不足，支付失败");
//        }
//        userService.consumption(userId, amount);
//    }
//
//    private void saveOrderRecords(OrderInfo order, BuyTicketDTO dto, ScenicInfo scenic) {
//        order.setStatus("已支付");
//        orderMapper.insert(order);
//        OrderItem orderItem = new OrderItem();
//        orderItem.setOrderId(order.getId());
//        orderItem.setAttractionId(dto.getId());
//        orderItem.setQuantity(dto.getNumber());
//        orderItem.setPrice(scenic.getPrice());
//        Notice notice = new Notice();
//        notice.setTypeCode("订单");
//        notice.setUserId(order.getUserId());
//        notice.setContent("您的订单已支付，订单号为：" + order.getOrderNo());
//        notice.setIsRead("未读");
//        notice.setTitle("订单支付");
//        noticeMapper.insert(notice);
//        orderItemMapper.insert(orderItem);
//    }
}
