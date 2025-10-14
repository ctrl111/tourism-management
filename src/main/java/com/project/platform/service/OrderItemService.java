package com.project.platform.service;

//import com.project.platform.dto.BuyTicketDTO;
import com.project.platform.entity.OrderItem;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 订单详情
 */
public interface OrderItemService {

    PageVO<OrderItem> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    OrderItem selectById(Integer id);

    List<OrderItem> list();

    void insert(OrderItem entity);

    void updateById(OrderItem entity);

    void removeByIds(List<Integer> id);

}
