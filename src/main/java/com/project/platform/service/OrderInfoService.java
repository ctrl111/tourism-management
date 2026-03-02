package com.project.platform.service;

import com.project.platform.dto.BuyTicketDTO;
import com.project.platform.entity.OrderInfo;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 订单信息
 */
public interface OrderInfoService {

    PageVO<OrderInfo> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    OrderInfo selectById(Integer id);

    List<OrderInfo> list();

    void insert(OrderInfo entity);

    void updateById(OrderInfo entity);

    void removeByIds(List<Integer> id);

    String buyTickets(BuyTicketDTO dto);
}
