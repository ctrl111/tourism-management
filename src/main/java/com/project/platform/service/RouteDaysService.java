package com.project.platform.service;

import com.project.platform.entity.RouteDays;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 地点表
 */
public interface RouteDaysService {

    PageVO<RouteDays> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    RouteDays selectById(Integer id);

    List<RouteDays> list();

    void insert(RouteDays entity);

    void updateById(RouteDays entity);

    void removeByIds(List<Integer> id);
}
