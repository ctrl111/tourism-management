package com.project.platform.service;

import com.project.platform.entity.CommentInfo;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 评论记录
 */
public interface CommentInfoService {

    PageVO<CommentInfo> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    CommentInfo selectById(Integer id);

    List<CommentInfo> list();

    void insert(CommentInfo entity);

    void updateById(CommentInfo entity);

    void removeByIds(List<Integer> id);

    Map<String, Object> getStatistics();

}
