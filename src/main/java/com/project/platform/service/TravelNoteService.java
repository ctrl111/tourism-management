package com.project.platform.service;

import com.project.platform.entity.TravelNote;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 游记分享信息
 */
public interface TravelNoteService {

    PageVO<TravelNote> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    PageVO<TravelNote> homePage(Map<String, Object> query, Integer pageNum, Integer pageSize);

    TravelNote selectById(Integer id);

    void  putViewCount(Integer id);

    List<TravelNote> list();

    void insert(TravelNote entity);

    void updateById(TravelNote entity);

    void removeByIds(List<Integer> id);
}
