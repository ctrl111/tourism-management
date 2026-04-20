package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.*;
import com.project.platform.mapper.CommentInfoMapper;
import com.project.platform.mapper.FavoriteMapper;
import com.project.platform.mapper.ScenicInfoMapper;
import com.project.platform.mapper.UserMapper;
import com.project.platform.service.CollaborativeFilteringService;
import com.project.platform.service.FileService;
import com.project.platform.service.ScenicInfoService;
import com.project.platform.utils.CurrentUserThreadLocal;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 景点信息
 */
@Service
@Slf4j
public class ScenicInfoServiceImpl  implements ScenicInfoService {
    @Resource
    private ScenicInfoMapper scenicInfoMapper;
    @Autowired
    private CommentInfoMapper commentInfoMapper;
    @Resource
    private FavoriteMapper favoriteMapper;
    @Resource
    private FileService fileService;
    @Resource
    private CollaborativeFilteringService collaborativeFilteringService;


    @Override
    public PageVO<ScenicInfo> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<ScenicInfo> page = new PageVO();
        List<ScenicInfo> list = scenicInfoMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        list.forEach(scenicInfo -> {
            // 使用CommentInfo统一评论表
            // 总评论数量（typeCode='SCENIC', associationId=景点ID）
            int countComm = commentInfoMapper.queryCommentsCount("SCENIC", scenicInfo.getId());
            scenicInfo.setCountComment(countComm);
            
            // 统计收藏数
            int countFav = favoriteMapper.queryFavoriteCount("SCENIC", scenicInfo.getId());
            scenicInfo.setCountFavorite(countFav);
            
            // 计算平均评分（如果CommentInfo有rating字段）
            // 注：CommentInfo表没有rating字段，暂时设置为0或根据点赞数计算
            scenicInfo.setScore(0.0);
        });
        page.setList(list);
        page.setTotal(scenicInfoMapper.queryCount(query));
        return page;
    }

    /**
     * 根据用户画像（协同过滤推荐）获取数据
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<ScenicInfo> homePage(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        CurrentUserDTO dto = CurrentUserThreadLocal.getCurrentUser();
        PageVO<ScenicInfo> page = new PageVO();
        List<ScenicInfo> list = new ArrayList<>();
        
        // 如果用户已登录，使用协同过滤推荐
        if (dto != null && dto.getId() != null && "USER".equals(dto.getType())) {
            log.info("为用户{}生成协同过滤推荐", dto.getId());
            
            try {
                // 使用协同过滤算法推荐
                List<ScenicInfo> cfRecommendations = collaborativeFilteringService.recommendForUser(dto.getId(), pageSize * 2);
                
                if (!cfRecommendations.isEmpty()) {
                    log.info("协同过滤推荐成功，推荐了{}个景点", cfRecommendations.size());
                    // 分页处理推荐结果
                    int start = (pageNum - 1) * pageSize;
                    int end = Math.min(start + pageSize, cfRecommendations.size());
                    
                    if (start < cfRecommendations.size()) {
                        list = cfRecommendations.subList(start, end);
                    }
                }
            } catch (Exception e) {
                log.error("协同过滤推荐失败，降级到普通推荐", e);
            }
        }
        
        // 如果推荐结果不足（未登录、推荐失败、或推荐数量不够），补充热门景点
        if (list.size() < pageSize) {
            log.info("推荐结果不足，补充热门景点");
            
            // 提取已有的景点ID，避免重复
            Set<Integer> existingIds = list.stream()
                    .map(ScenicInfo::getId)
                    .collect(Collectors.toSet());
            
            // 查询热门景点（按ID倒序）
            List<ScenicInfo> popularScenics = scenicInfoMapper.queryPage(
                    (pageNum - 1) * pageSize, 
                    pageSize, 
                    query
            );
            
            // 补充到结果中
            int needed = pageSize - list.size();
            for (ScenicInfo scenic : popularScenics) {
                if (needed <= 0) break;
                if (!existingIds.contains(scenic.getId())) {
                    list.add(scenic);
                    existingIds.add(scenic.getId());
                    needed--;
                }
            }
            
            // 如果还不够，从全量数据补充
            if (list.size() < pageSize) {
                List<ScenicInfo> allScenics = scenicInfoMapper.list();
                for (ScenicInfo scenic : allScenics) {
                    if (list.size() >= pageSize) break;
                    if (!existingIds.contains(scenic.getId())) {
                        list.add(scenic);
                        existingIds.add(scenic.getId());
                    }
                }
            }
        }
        
        // 填充额外信息（评论数、收藏数等）
        list.forEach(scenicInfo -> {
            // 使用CommentInfo统一评论表
            int countComm = commentInfoMapper.queryCommentsCount("SCENIC", scenicInfo.getId());
            scenicInfo.setCountComment(countComm);
            
            // 统计收藏数
            int countFav = favoriteMapper.queryFavoriteCount("SCENIC", scenicInfo.getId());
            scenicInfo.setCountFavorite(countFav);
            
            scenicInfo.setScore(0.0);
        });
        
        page.setList(list);
        page.setTotal(list.size());
        
        return page;
    }

    @Override
    public ScenicInfo selectById(Integer id) {
        CurrentUserDTO dto =  CurrentUserThreadLocal.getCurrentUser();
        ScenicInfo scenicInfo = scenicInfoMapper.selectById(id);
        if (scenicInfo != null) {
            // 使用CommentInfo统一评论表
            int countComm = commentInfoMapper.queryCommentsCount("SCENIC", scenicInfo.getId());
            scenicInfo.setCountComment(countComm);
            
            // 统计收藏数
            int countFav = favoriteMapper.queryFavoriteCount("SCENIC", scenicInfo.getId());
            scenicInfo.setCountFavorite(countFav);
            
            scenicInfo.setScore(0.0);
            
            // 只有登录用户才检查是否收藏
            if (dto != null && dto.getId() != null) {
                System.out.println("=== 检查收藏状态 ===");
                System.out.println("用户ID: " + dto.getId());
                System.out.println("景点ID: " + scenicInfo.getId());
                System.out.println("类型: SCENIC");
                int isFavorited = favoriteMapper.queryIsFavorite("SCENIC", scenicInfo.getId(), dto.getId());
                System.out.println("查询结果: " + isFavorited);
                scenicInfo.setFavorited(isFavorited > 0);
                System.out.println("设置收藏状态: " + scenicInfo.getFavorited());
            } else {
                System.out.println("=== 用户未登录，设置收藏状态为false ===");
                scenicInfo.setFavorited(false);
            }
        }
        return scenicInfo;
    }

    @Override
    public void  putViewCount(Integer id){
        // 不再记录浏览历史
        // 协同过滤算法现在只基于收藏和购票数据
        log.debug("景点{}被访问，但不再记录浏览历史", id);
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
        
        // 如果更新了图片，删除旧图片文件
        ScenicInfo oldEntity = scenicInfoMapper.selectById(entity.getId());
        if (oldEntity != null) {
            // 删除旧封面图
            if (StringUtils.isNotBlank(entity.getCoverImage()) 
                && !entity.getCoverImage().equals(oldEntity.getCoverImage())
                && StringUtils.isNotBlank(oldEntity.getCoverImage())) {
                fileService.deleteFileByUrl(oldEntity.getCoverImage());
                log.info("删除旧封面图: scenicId={}, oldCoverImage={}", entity.getId(), oldEntity.getCoverImage());
            }
            
            // 删除旧详情图（可能是多张，逗号分隔）
            if (StringUtils.isNotBlank(entity.getDetailImages()) 
                && !entity.getDetailImages().equals(oldEntity.getDetailImages())
                && StringUtils.isNotBlank(oldEntity.getDetailImages())) {
                String[] oldImages = oldEntity.getDetailImages().split(",");
                String[] newImages = entity.getDetailImages().split(",");
                List<String> newImageList = Arrays.asList(newImages);
                
                for (String oldImage : oldImages) {
                    if (!newImageList.contains(oldImage.trim())) {
                        fileService.deleteFileByUrl(oldImage.trim());
                        log.info("删除旧详情图: scenicId={}, oldDetailImage={}", entity.getId(), oldImage.trim());
                    }
                }
            }
        }
        
        scenicInfoMapper.updateById(entity);
    }
    private void check(ScenicInfo entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        for (Integer id : ids) {
            ScenicInfo scenicInfo = scenicInfoMapper.selectById(id);
            if (scenicInfo == null) {
                continue;
            }
            
            // 检查关联数据
            int orderCount = orderInfoMapper.countByScenicId(id);
            int favoriteCount = favoriteMapper.queryFavoriteCount("SCENIC", id);
            int commentCount = commentInfoMapper.queryCommentsCount("SCENIC", id);
            
            log.info("准备删除景点ID={}, 名称={}, 关联数据：订单{}条, 收藏{}条, 评论{}条", 
                id, scenicInfo.getName(), orderCount, favoriteCount, commentCount);
            
            // 删除关联的订单
            if (orderCount > 0) {
                List<OrderInfo> orders = orderInfoMapper.queryByScenicId(id);
                List<Integer> orderIds = orders.stream()
                        .map(OrderInfo::getId)
                        .collect(Collectors.toList());
                orderInfoMapper.removeByIds(orderIds);
                log.info("删除景点ID={}的{}条订单", id, orderCount);
            }
            
            // 删除关联的收藏
            if (favoriteCount > 0) {
                List<Favorite> favorites = favoriteMapper.queryFavoriteList("SCENIC", id);
                List<Integer> favoriteIds = favorites.stream()
                        .map(Favorite::getId)
                        .collect(Collectors.toList());
                favoriteMapper.removeByIds(favoriteIds);
                log.info("删除景点ID={}的{}条收藏", id, favoriteCount);
            }
            
            // 删除关联的评论
            if (commentCount > 0) {
                List<CommentInfo> commentInfos = commentInfoMapper.queryCommentsList("SCENIC", id);
                List<Integer> commentIds = commentInfos.stream()
                        .map(CommentInfo::getId)
                        .collect(Collectors.toList());
                commentInfoMapper.removeByIds(commentIds);
                log.info("删除景点ID={}的{}条评论", id, commentCount);
            }
            
            // 删除景点图片文件
            if (StringUtils.isNotBlank(scenicInfo.getCoverImage())) {
                fileService.deleteFileByUrl(scenicInfo.getCoverImage());
                log.info("删除景点封面图: scenicId={}, coverImage={}", id, scenicInfo.getCoverImage());
            }
            
            if (StringUtils.isNotBlank(scenicInfo.getDetailImages())) {
                String[] images = scenicInfo.getDetailImages().split(",");
                for (String image : images) {
                    fileService.deleteFileByUrl(image.trim());
                    log.info("删除景点详情图: scenicId={}, detailImage={}", id, image.trim());
                }
            }
        }
        
        scenicInfoMapper.removeByIds(ids);
        log.info("景点删除完成，共删除{}个景点", ids.size());
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
