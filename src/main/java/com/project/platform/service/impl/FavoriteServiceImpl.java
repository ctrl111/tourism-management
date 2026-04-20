package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.Favorite;
import com.project.platform.mapper.FavoriteMapper;
import com.project.platform.service.FavoriteService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.exception.CustomException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 收藏信息
 */
@Service
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {
    @Resource
    private FavoriteMapper favoriteMapper;

    @Override
    public PageVO<Favorite> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        // 获取当前登录用户，只查询当前用户的收藏
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser != null && currentUser.getId() != null) {
            query.put("userId", currentUser.getId());
        }
        
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
        // 获取当前登录用户
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser != null && currentUser.getId() != null) {
            entity.setUserId(currentUser.getId());
        }
        
        System.out.println("=== 添加收藏 ===");
        System.out.println("用户ID: " + entity.getUserId());
        System.out.println("类型: " + entity.getTypeCode());
        System.out.println("关联ID: " + entity.getAssociationId());
        
        // 设置创建时间
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(LocalDateTime.now());
        }
        
        // 检查是否已经收藏
        if (entity.getUserId() != null && entity.getTypeCode() != null && entity.getAssociationId() != null) {
            int count = favoriteMapper.queryIsFavorite(entity.getTypeCode(), entity.getAssociationId(), entity.getUserId());
            System.out.println("已收藏数量: " + count);
            if (count > 0) {
                throw new CustomException("Уже добавлено в избранное");
            }
        }
        
        check(entity);
        favoriteMapper.insert(entity);
        System.out.println("Избранное добавлено успешно");
    }
    
    @Override
    public void deleteFavorite(Favorite entity) {
        // 获取当前登录用户
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser != null && currentUser.getId() != null) {
            entity.setUserId(currentUser.getId());
        }
        
        if (entity.getUserId() == null || entity.getTypeCode() == null || entity.getAssociationId() == null) {
            throw new CustomException("Неполные данные");
        }
        
        favoriteMapper.deleteFavorite(entity.getTypeCode(), entity.getAssociationId(), entity.getUserId());
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
