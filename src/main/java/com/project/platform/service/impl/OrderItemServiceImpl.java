package com.project.platform.service.impl;

import com.project.platform.entity.OrderItem;
import com.project.platform.entity.ScenicInfo;
import com.project.platform.entity.User;
import com.project.platform.mapper.OrderItemMapper;
import com.project.platform.mapper.ScenicInfoMapper;
import com.project.platform.mapper.UserMapper;
import com.project.platform.service.OrderItemService;
import com.project.platform.utils.CurrentUserThreadLocal;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 订单详情
 */
@Service
public class OrderItemServiceImpl  implements OrderItemService {
    @Resource
    private OrderItemMapper orderItemMapper;


    @Override
    public PageVO<OrderItem> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<OrderItem> page = new PageVO();
        List<OrderItem> list = orderItemMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(orderItemMapper.queryCount(query));
        return page;
    }

    @Override
    public OrderItem selectById(Integer id) {
        OrderItem orderItem = orderItemMapper.selectById(id);
        return orderItem;
    }

    @Override
    public List<OrderItem> list() {
        return orderItemMapper.list();
    }
    @Override
    public void insert(OrderItem entity) {
        check(entity);
        orderItemMapper.insert(entity);
    }
    @Override
    public void updateById(OrderItem entity) {
        check(entity);
        orderItemMapper.updateById(entity);
    }
    private void check(OrderItem entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        orderItemMapper.removeByIds(ids);
    }

}
