package com.project.platform.service;

import com.project.platform.entity.ScenicComment;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 景点评论信息
 */
public interface ScenicCommentService {

    PageVO<ScenicComment> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    ScenicComment selectById(Integer id);

    List<ScenicComment> list();

    void insert(ScenicComment entity);

    void updateById(ScenicComment entity);

    void removeByIds(List<Integer> id);

    List<ScenicComment> queryCommentsList(Integer id);
}
