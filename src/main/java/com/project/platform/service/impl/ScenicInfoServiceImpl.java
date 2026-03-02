package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.*;
import com.project.platform.mapper.CommentInfoMapper;
import com.project.platform.mapper.ScenicInfoMapper;
import com.project.platform.mapper.UserMapper;
import com.project.platform.mapper.ViewHistoryMapper;
import com.project.platform.service.ScenicInfoService;
import com.project.platform.utils.CurrentUserThreadLocal;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 景点信息
 */
@Service
public class ScenicInfoServiceImpl  implements ScenicInfoService {
    @Resource
    private ScenicInfoMapper scenicInfoMapper;
    @Resource
    private ViewHistoryMapper viewHistoryMapper;
    @Autowired
    private CommentInfoMapper commentInfoMapper;


    @Override
    public PageVO<ScenicInfo> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<ScenicInfo> page = new PageVO();
        List<ScenicInfo> list = scenicInfoMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        list.forEach(scenicInfo -> {
            // 使用CommentInfo统一评论表
            // 总评论数量（typeCode='景点', associationId=景点ID）
            int countComm = commentInfoMapper.queryCommentsCount("景点", scenicInfo.getId());
            scenicInfo.setCountComment(countComm);
            // 计算平均评分（如果CommentInfo有rating字段）
            // 注：CommentInfo表没有rating字段，暂时设置为0或根据点赞数计算
            scenicInfo.setScore(0.0);
        });
        page.setList(list);
        page.setTotal(scenicInfoMapper.queryCount(query));
        return page;
    }

    /**
     * 根据用户画像（浏览次数）获取数据
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<ScenicInfo> homePage(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        CurrentUserDTO dto =  CurrentUserThreadLocal.getCurrentUser();
        
        // 如果用户已登录，基于用户浏览历史推荐
        if (dto != null && dto.getId() != null) {
            List<ViewHistory> views = viewHistoryMapper.queryViewList("景点", dto.getId());
            List<Integer> intList = views.stream().map(ViewHistory::getAssociationId).distinct().collect(Collectors.toList());
            if (intList.size() > pageSize) {
                query.put("associationIds", intList);
            }
        }
        // 如果用户未登录（dto为null），则返回普通列表数据
        
        query.put("pageNum",pageNum);
        PageVO<ScenicInfo> page = new PageVO();
        List<ScenicInfo> list = scenicInfoMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        if (list.size() < pageSize) {
            List<ScenicInfo> ls =  scenicInfoMapper.list();
            // 提取当前列表中的 ID 集合，用于去重
            Set<Integer> existingIds = list.stream()
                    .map(ScenicInfo::getId)
                    .collect(Collectors.toSet());
            // 计算需要补充的数量
            int needed = pageSize - list.size();
            List<ScenicInfo> supplement = new ArrayList<>();
            // 遍历全量数据，筛选未重复的元素
            for (ScenicInfo scenic : ls) {
                if (supplement.size() >= needed) {
                    break; // 已补充足够数量，提前退出
                }
                if (!existingIds.contains(scenic.getId())) {
                    supplement.add(scenic);
                    existingIds.add(scenic.getId()); // 更新已存在的 ID，避免后续重复
                }
            }
            // 将补充的数据添加到原列表
            list.addAll(supplement);
        }
        list.forEach(scenicInfo -> {
            // 使用CommentInfo统一评论表
            int countComm = commentInfoMapper.queryCommentsCount("景点", scenicInfo.getId());
            scenicInfo.setCountComment(countComm);
            scenicInfo.setScore(0.0);
        });
        page.setList(list);
        page.setTotal(scenicInfoMapper.queryCount(query));
        return page;
    }

    @Override
    public ScenicInfo selectById(Integer id) {
        CurrentUserDTO dto =  CurrentUserThreadLocal.getCurrentUser();
        ScenicInfo scenicInfo = scenicInfoMapper.selectById(id);
        if (scenicInfo != null) {
            // 使用CommentInfo统一评论表
            int countComm = commentInfoMapper.queryCommentsCount("景点", scenicInfo.getId());
            scenicInfo.setCountComment(countComm);
            scenicInfo.setScore(0.0);
        }
        return scenicInfo;
    }

    @Override
    public void  putViewCount(Integer id){
        CurrentUserDTO dto =  CurrentUserThreadLocal.getCurrentUser();
        ScenicInfo scenicInfo = scenicInfoMapper.selectById(id);
        if (scenicInfo != null) {
            // 只有已登录的普通用户才记录浏览历史
            if (dto != null && dto.getId() != null && "USER".equals(dto.getType())) {
                ViewHistory entity = new ViewHistory();
                entity.setTypeCode("景点");
                entity.setUserId(dto.getId());
                entity.setAssociationId(id);
                viewHistoryMapper.insert(entity);
            }
        }
    }

    @Override
    public List<ScenicInfo> list() {
        return scenicInfoMapper.list();
    }
    @Override
    public void insert(ScenicInfo entity) {
        check(entity);
        scenicInfoMapper.insert(entity);
    }
    @Override
    public void updateById(ScenicInfo entity) {
        check(entity);
        scenicInfoMapper.updateById(entity);
    }
    private void check(ScenicInfo entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        for (Integer id : ids) {
            // 删除评论信息（使用CommentInfo统一表）
            List<CommentInfo> commentInfos = commentInfoMapper.queryCommentsList("景点", id);
            if (commentInfos != null && !commentInfos.isEmpty()) {
                List<Integer> commentIds = commentInfos.stream()
                        .map(CommentInfo::getId)
                        .collect(Collectors.toList());
                commentInfoMapper.removeByIds(commentIds);
            }
            
            // 删除浏览历史
            List<ViewHistory> viewList = viewHistoryMapper.queryViewList("景点", id);
            if (viewList != null && !viewList.isEmpty()) {
                List<Integer> viewIds = viewList.stream()
                        .map(ViewHistory::getId)
                        .collect(Collectors.toList());
                viewHistoryMapper.removeByIds(viewIds);
            }
        }
        scenicInfoMapper.removeByIds(ids);
    }

    // 计算简单算术平均分
    public static double calculateAverageRating(List<Double> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0; // 无评分时返回0
        }
        double sum = 0.0;
        for (Double rating : ratings) {
            sum += rating;
        }
        double average = sum / ratings.size();
        // 四舍五入到一位小数
        return Math.round(average * 10) / 10.0;
    }
}
