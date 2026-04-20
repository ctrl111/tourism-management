package com.project.platform.mapper;

import com.project.platform.entity.OrderInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface OrderInfoMapper {
    List<OrderInfo> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM order_info WHERE id = #{id}")
    OrderInfo selectById(Integer id);

    @Select("SELECT * FROM order_info WHERE order_no = #{orderNo}")
    OrderInfo selectByOrderNo(String orderNo);

    @Select("SELECT * FROM order_info WHERE user_id = #{userId}")
    List<OrderInfo> selectByUserId(Integer userId);

    @Select("SELECT COUNT(*) FROM order_info WHERE scenic_id = #{scenicId}")
    int countByScenicId(Integer scenicId);

    @Select("SELECT * FROM order_info WHERE scenic_id = #{scenicId}")
    List<OrderInfo> queryByScenicId(Integer scenicId);

    @Select("SELECT * FROM order_info")
    List<OrderInfo> list();

    int insert(OrderInfo entity);

    int updateById(OrderInfo entity);

    boolean removeByIds(List<Integer> ids);

}