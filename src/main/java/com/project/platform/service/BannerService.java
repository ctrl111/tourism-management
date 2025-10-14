package com.project.platform.service;

import com.project.platform.entity.Banner;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 轮播图信息
 */
public interface BannerService {

    PageVO<Banner> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    Banner selectById(Integer id);

    List<Banner> list();

    void insert(Banner entity);

    void updateById(Banner entity);

    void removeByIds(List<Integer> id);
}
