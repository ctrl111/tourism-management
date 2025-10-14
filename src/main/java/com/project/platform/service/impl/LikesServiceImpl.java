package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.Favorite;
import com.project.platform.entity.Likes;
import com.project.platform.mapper.*;
import com.project.platform.service.FavoriteService;
import com.project.platform.service.LikesService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 收藏信息
 */
@Service
public class LikesServiceImpl implements LikesService {
    @Resource
    private LikesMapper likesMapper;
    @Resource
    private TravelNoteMapper travelNoteMapper;
    @Resource
    private RouteMapper routeMapper;

    @Override
    public PageVO<Likes> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        CurrentUserDTO userDTO = CurrentUserThreadLocal.getCurrentUser();
        if (userDTO.getType().equals("USER")){
            query.put("userId", userDTO.getId());
        }
        PageVO<Likes> page = new PageVO();
        List<Likes> list = likesMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        list.forEach(item->{
            String type = item.getTypeCode();
            if (type.equals("游记")){
                item.setAssociationObject(travelNoteMapper.selectById(item.getAssociationId()));
            }
            if (type.equals("路线分享")){
                item.setAssociationObject(routeMapper.selectById(item.getAssociationId()));
            }
        });
        page.setList(list);
        page.setTotal(likesMapper.queryCount(query));
        return page;
    }

    @Override
    public Likes selectById(Integer id) {
        Likes collection = likesMapper.selectById(id);
        return collection;
    }

    @Override
    public List<Likes> list() {
        return likesMapper.list();
    }
    @Override
    public void insert(Likes entity) {
        entity.setUserId(CurrentUserThreadLocal.getCurrentUser().getId());
        check(entity);
        likesMapper.insert(entity);
    }
    @Override
    public void del(Likes entity) {
        entity.setUserId(CurrentUserThreadLocal.getCurrentUser().getId());
        check(entity);
        likesMapper.deleteByUserId(entity);
    }
    @Override
    public void updateById(Likes entity) {
        check(entity);
        likesMapper.updateById(entity);
    }
    private void check(Likes entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        likesMapper.removeByIds(ids);
    }
}
