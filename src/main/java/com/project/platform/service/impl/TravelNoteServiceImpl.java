package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.*;
import com.project.platform.mapper.*;
import com.project.platform.service.FileService;
import com.project.platform.service.TravelNoteService;
import com.project.platform.utils.CurrentUserThreadLocal;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 游记分享信息
 */
@Service
@Slf4j
public class TravelNoteServiceImpl  implements TravelNoteService {
    @Resource
    private TravelNoteMapper travelNoteMapper;
    @Resource
    private UserMapper userService;
    @Resource
    private CommentInfoMapper commentInfoMapper;
    @Resource
    private FavoriteMapper favoriteMapper;
    @Resource
    private FileService fileService;

    @Override
    public PageVO<TravelNote> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<TravelNote> page = new PageVO();
        List<TravelNote> list = travelNoteMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        list.forEach(travelNote -> {
            //发布人信息
            User user = userService.selectById(travelNote.getUserId());
            travelNote.setUser(user);
            //评论数
            Integer commentCount = commentInfoMapper.queryCommentsCount("TRAVEL_NOTE",travelNote.getId());
            travelNote.setCommentsCount(commentCount);
            //浏览数（不再记录浏览历史，设置为0）
            travelNote.setViewCount(0);
            //收藏数
            Integer favoriteCount = favoriteMapper.queryFavoriteCount("TRAVEL_NOTE",travelNote.getId());
            travelNote.setFavoriteCount(favoriteCount);
            travelNote.setLikesCount(favoriteCount); // 保持兼容性
        });
        page.setList(list);
        page.setTotal(travelNoteMapper.queryCount(query));
        return page;
    }

    @Override
    public PageVO<TravelNote> homePage(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        log.info("========== 开始获取首页游记列表 ==========");
        log.info("参数：pageNum={}, pageSize={}, query={}", pageNum, pageSize, query);
        
        PageVO<TravelNote> page = new PageVO();
        
        // 获取所有游记
        List<TravelNote> allNotes = travelNoteMapper.list();
        log.info("从数据库获取到{}条游记", allNotes.size());
        
        // 填充每个游记的统计信息
        allNotes.forEach(travelNote -> {
            // 发布人信息
            User user = userService.selectById(travelNote.getUserId());
            travelNote.setUser(user);
            // 评论数
            Integer commentCount = commentInfoMapper.queryCommentsCount("TRAVEL_NOTE", travelNote.getId());
            travelNote.setCommentsCount(commentCount);
            // 浏览数（不再记录浏览历史，设置为0）
            travelNote.setViewCount(0);
            // 收藏数
            Integer favoriteCount = favoriteMapper.queryFavoriteCount("TRAVEL_NOTE", travelNote.getId());
            travelNote.setFavoriteCount(favoriteCount);
            travelNote.setLikesCount(favoriteCount);
            
            log.debug("游记ID:{}, 标题:{}, 收藏数:{}, 评论数:{}", 
                travelNote.getId(), travelNote.getTitle(), favoriteCount, commentCount);
        });
        
        // 按热度排序：收藏数 + 评论数（降序）
        allNotes.sort((n1, n2) -> {
            Integer fav1 = n1.getFavoriteCount();
            Integer comm1 = n1.getCommentsCount();
            Integer fav2 = n2.getFavoriteCount();
            Integer comm2 = n2.getCommentsCount();
            
            int hot1 = (fav1 != null ? fav1 : 0) + (comm1 != null ? comm1 : 0);
            int hot2 = (fav2 != null ? fav2 : 0) + (comm2 != null ? comm2 : 0);
            
            return Integer.compare(hot2, hot1); // 降序
        });
        
        log.info("按热度排序完成");
        
        // 分页
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allNotes.size());
        List<TravelNote> pagedList = start < allNotes.size() ? allNotes.subList(start, end) : new ArrayList<>();
        
        page.setList(pagedList);
        page.setTotal(allNotes.size());
        
        log.info("========== 首页游记列表：按热度排序，返回{}条记录（总共{}条） ==========", pagedList.size(), allNotes.size());
        
        return page;
    }

    @Override
    public TravelNote selectById(Integer id) {
        CurrentUserDTO dto =  CurrentUserThreadLocal.getCurrentUser();
        TravelNote travelNote = travelNoteMapper.selectById(id);
        if (travelNote != null) {
            User user = userService.selectById(travelNote.getUserId());
            travelNote.setUser(user);
            //评论数
            Integer commentCount = commentInfoMapper.queryCommentsCount("TRAVEL_NOTE",travelNote.getId());
            travelNote.setCommentsCount(commentCount);
            //浏览数（不再记录浏览历史，设置为0）
            travelNote.setViewCount(0);
            //收藏数
            Integer favoriteCount = favoriteMapper.queryFavoriteCount("TRAVEL_NOTE",travelNote.getId());
            travelNote.setFavoriteCount(favoriteCount);
            travelNote.setLikesCount(favoriteCount); // 保持兼容性
            
            // 只有登录用户才检查是否收藏
            if (dto != null && dto.getId() != null && user != null) {
                int isFavorited = favoriteMapper.queryIsFavorite("TRAVEL_NOTE", travelNote.getId(), dto.getId());
                travelNote.setLiked(isFavorited > 0); // 保持兼容性
                travelNote.setFavorited(isFavorited > 0);
            } else {
                travelNote.setLiked(false);
                travelNote.setFavorited(false);
            }
        }
        return travelNote;
    }
    @Override
    public void  putViewCount(Integer id){
        // 不再记录浏览历史
        log.debug("游记{}被访问，但不再记录浏览历史", id);
    }
    @Override
    public List<TravelNote> list() {
        return travelNoteMapper.list();
    }
    @Override
    public void insert(TravelNote entity) {
        CurrentUserDTO userDTO = CurrentUserThreadLocal.getCurrentUser();
        entity.setUserId(userDTO.getId());
        check(entity);
        travelNoteMapper.insert(entity);
    }
    @Override
    public void updateById(TravelNote entity) {
        check(entity);
        
        // 如果更新了封面，删除旧封面文件
        TravelNote oldEntity = travelNoteMapper.selectById(entity.getId());
        if (oldEntity != null 
            && StringUtils.isNotBlank(entity.getCover()) 
            && !entity.getCover().equals(oldEntity.getCover())
            && StringUtils.isNotBlank(oldEntity.getCover())) {
            fileService.deleteFileByUrl(oldEntity.getCover());
            log.info("删除旧游记封面: travelNoteId={}, oldCover={}", entity.getId(), oldEntity.getCover());
        }
        
        travelNoteMapper.updateById(entity);
    }
    private void check(TravelNote entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        for (Integer id : ids) {
            TravelNote travelNote = travelNoteMapper.selectById(id);
            if (travelNote == null) {
                continue;
            }
            
            // 检查关联数据
            int favoriteCount = favoriteMapper.queryFavoriteCount("TRAVEL_NOTE", id);
            int commentCount = commentInfoMapper.queryCommentsCount("TRAVEL_NOTE", id);
            
            log.info("准备删除游记ID={}, 标题={}, 关联数据：收藏{}条, 评论{}条", 
                id, travelNote.getTitle(), favoriteCount, commentCount);
            
            // 删除游记封面文件
            if (StringUtils.isNotBlank(travelNote.getCover())) {
                fileService.deleteFileByUrl(travelNote.getCover());
                log.info("删除游记封面: travelNoteId={}, cover={}", id, travelNote.getCover());
            }
            
            // 删除关联的评论
            if (commentCount > 0) {
                List<CommentInfo> commentInfos = commentInfoMapper.queryCommentsList("TRAVEL_NOTE", id);
                List<Integer> commentIds = commentInfos.stream()
                        .map(CommentInfo::getId)
                        .collect(Collectors.toList());
                commentInfoMapper.removeByIds(commentIds);
                log.info("删除游记ID={}的{}条评论", id, commentCount);
            }
            
            // 删除关联的收藏
            if (favoriteCount > 0) {
                List<Favorite> favoriteList = favoriteMapper.queryFavoriteList("TRAVEL_NOTE", id);
                List<Integer> favoriteIds = favoriteList.stream()
                        .map(Favorite::getId)
                        .collect(Collectors.toList());
                favoriteMapper.removeByIds(favoriteIds);
                log.info("删除游记ID={}的{}条收藏", id, favoriteCount);
            }
        }
        
        travelNoteMapper.removeByIds(ids);
        log.info("游记删除完成，共删除{}条游记", ids.size());
    }
}
