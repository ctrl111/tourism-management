package com.project.platform.service;

import com.project.platform.entity.Favorite;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 收藏信息
 */
public interface FavoriteService {

    PageVO<Favorite> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    Favorite selectById(Integer id);

    List<Favorite> list();

    void insert(Favorite entity);

    void deleteFavorite(Favorite entity);

    void updateById(Favorite entity);

    void removeByIds(List<Integer> id);
}
