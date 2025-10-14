package com.project.platform.service;

import com.project.platform.entity.Favorite;
import com.project.platform.entity.Likes;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 点赞信息
 */
public interface LikesService {

    PageVO<Likes> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    Likes selectById(Integer id);

    List<Likes> list();

    void insert(Likes entity);

    void del(Likes entity);

    void updateById(Likes entity);

    void removeByIds(List<Integer> id);
}
