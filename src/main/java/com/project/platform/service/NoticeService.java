package com.project.platform.service;

import com.project.platform.entity.Notice;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 系统通知
 */
public interface NoticeService {

    PageVO<Notice> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    Notice selectById(Integer id);

    List<Notice> list();

    void insert(Notice entity);


    void updateById(Notice entity);

    void removeByIds(List<Integer> id);
}
