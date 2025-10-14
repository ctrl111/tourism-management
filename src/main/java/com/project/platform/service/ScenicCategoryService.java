package com.project.platform.service;

import com.project.platform.entity.ScenicCategory;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 景点分类信息
 */
public interface ScenicCategoryService {

    PageVO<ScenicCategory> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    ScenicCategory selectById(Integer id);

    List<ScenicCategory> list();

    void insert(ScenicCategory entity);

    void updateById(ScenicCategory entity);

    void removeByIds(List<Integer> id);
}
