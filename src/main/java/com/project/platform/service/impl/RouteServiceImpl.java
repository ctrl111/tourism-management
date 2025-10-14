package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.*;
import com.project.platform.mapper.*;
import com.project.platform.service.RouteService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    @Autowired
    private RouteMapper routeMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommentInfoMapper commentInfoMapper;
    @Resource
    private LikesMapper likesMapper;
    @Resource
    private ViewHistoryMapper viewHistoryMapper;

    @Override
    public Route selectById(Integer id) {
        Route route = routeMapper.selectById(id);
        return route;
    }

    @Override
    public PageVO<Route> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Route> page = new PageVO();
        List<Route> list = routeMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(routeMapper.queryCount(query));
        return page;
    }

    /**
     * 首页路线推荐（基于用户浏览历史的个性化推荐）
     */
    @Override
    public PageVO<Route> homePage(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        CurrentUserDTO dto = CurrentUserThreadLocal.getCurrentUser();
        List<ViewHistory> views = viewHistoryMapper.queryViewList("路线分享", dto.getId());
        List<Integer> intList = views.stream().map(ViewHistory::getAssociationId).distinct().collect(Collectors.toList());
        if (intList.size() > pageSize) {
            query.put("associationIds", intList);
        }
        query.put("pageNum", pageNum);
        PageVO<Route> page = new PageVO();
        List<Route> list = routeMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        
        // 如果推荐数据不足，补充其他路线数据
        if (list.size() < pageSize) {
            List<Route> ls = routeMapper.list();
            // 提取当前列表中的 ID 集合，用于去重
            Set<Integer> existingIds = list.stream()
                    .map(Route::getId)
                    .collect(Collectors.toSet());
            // 计算需要补充的数量
            int needed = pageSize - list.size();
            List<Route> supplement = new ArrayList<>();
            // 遍历全量数据，筛选未重复的元素
            for (Route route : ls) {
                if (supplement.size() >= needed) {
                    break; // 已补充足够数量，提前退出
                }
                if (!existingIds.contains(route.getId())) {
                    supplement.add(route);
                    existingIds.add(route.getId()); // 更新已存在的 ID，避免后续重复
                }
            }
            // 将补充的数据添加到原列表
            list.addAll(supplement);
        }
        
        // 填充关联数据
        list.forEach(route -> {
            // 发布人信息
            User user = userMapper.selectById(route.getUserId());
            route.setUser(user);
            // 评论数
            Integer commentCount = commentInfoMapper.queryCommentsCount("路线分享", route.getId());
            route.setCommentsCount(commentCount);
            // 浏览数
            Integer viewCount = viewHistoryMapper.queryViewCount("路线分享", route.getId());
            route.setViewCount(viewCount);
            // 点赞数
            Integer likeCount = likesMapper.queryLikeCount("路线分享", route.getId());
            route.setLikesCount(likeCount);
        });
        
        page.setList(list);
        page.setTotal(routeMapper.queryCount(query));
        return page;
    }
}
