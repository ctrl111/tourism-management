package com.project.platform.service.impl;

import com.project.platform.entity.RouteDays;
import com.project.platform.mapper.RouteDaysMapper;
import com.project.platform.service.RouteDaysService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 地点表
 */
@Service
public class RouteDaysServiceImpl implements RouteDaysService {
    @Resource
    private RouteDaysMapper routeScheduleMapper;

    @Override
    public PageVO<RouteDays> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<RouteDays> page = new PageVO();
        List<RouteDays> list = routeScheduleMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(routeScheduleMapper.queryCount(query));
        return page;
    }

    @Override
    public RouteDays selectById(Integer id) {
        RouteDays poi = routeScheduleMapper.selectById(id);
        return poi;
    }

    @Override
    public List<RouteDays> list() {
        return routeScheduleMapper.list();
    }
    @Override
    public void insert(RouteDays entity) {
        check(entity);
        routeScheduleMapper.insert(entity);
    }
    @Override
    public void updateById(RouteDays entity) {
        check(entity);
        routeScheduleMapper.updateById(entity);
    }
    private void check(RouteDays entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        routeScheduleMapper.removeByIds(ids);
    }
}
