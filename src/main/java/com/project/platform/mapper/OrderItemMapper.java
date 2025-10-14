package com.project.platform.mapper;

import com.project.platform.entity.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface OrderItemMapper {
    List<OrderItem> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM order_item WHERE id = #{id}")
    OrderItem selectById(Integer id);

    @Select("SELECT * FROM order_item")
    List<OrderItem> list();

    int insert(OrderItem entity);

    int updateById(OrderItem entity);

    boolean removeByIds(List<Integer> ids);

    @Select("SELECT order_item.*,scenic_info.NAME as scenicName FROM order_item  LEFT JOIN  scenic_info ON scenic_info.id = order_item.attraction_id WHERE order_id = #{id}")
    List<OrderItem> selectByOrderId(Integer id);

}