package com.project.platform.service;

import com.project.platform.entity.Route;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

public interface RouteService{


    Route selectById(Integer id);

    PageVO<Route> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    /**
     * 首页路线推荐（基于用户浏览历史）
     */
    PageVO<Route> homePage(Map<String, Object> query, Integer pageNum, Integer pageSize);

    void putViewCount(Integer id);

    List<Route> list();

    void insert(Route entity);

    void updateById(Route entity);

    void removeByIds(List<Integer> ids);

}