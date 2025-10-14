package com.project.platform.service.impl;

import com.project.platform.entity.Favorite;
import com.project.platform.mapper.FavoriteMapper;
import com.project.platform.service.FavoriteService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 收藏信息
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Resource
    private FavoriteMapper favoriteMapper;

    @Override
    public PageVO<Favorite> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Favorite> page = new PageVO();
        List<Favorite> list = favoriteMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(favoriteMapper.queryCount(query));
        return page;
    }

    @Override
    public Favorite selectById(Integer id) {
        Favorite collection = favoriteMapper.selectById(id);
        return collection;
    }

    @Override
    public List<Favorite> list() {
        return favoriteMapper.list();
    }
    @Override
    public void insert(Favorite entity) {
        check(entity);
        favoriteMapper.insert(entity);
    }
    @Override
    public void updateById(Favorite entity) {
        check(entity);
        favoriteMapper.updateById(entity);
    }
    private void check(Favorite entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        favoriteMapper.removeByIds(ids);
    }
}
