package com.project.platform.service;

import com.project.platform.entity.ViewHistory;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 浏览历史
 */
public interface ViewHistoryService {

    PageVO<ViewHistory> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    ViewHistory selectById(Integer id);

    List<ViewHistory> list();

    void insert(ViewHistory entity);

    void updateById(ViewHistory entity);

    void removeByIds(List<Integer> id);
}
