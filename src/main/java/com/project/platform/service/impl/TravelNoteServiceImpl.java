package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.*;
import com.project.platform.mapper.*;
import com.project.platform.service.TravelNoteService;
import com.project.platform.utils.CurrentUserThreadLocal;
import jakarta.annotation.Resource;
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
public class TravelNoteServiceImpl  implements TravelNoteService {
    @Resource
    private TravelNoteMapper travelNoteMapper;
    @Resource
    private UserMapper userService;
    @Resource
    private CommentInfoMapper commentInfoMapper;
    @Resource
    private LikesMapper likesMapper;
    @Resource
    private FavoriteMapper favoriteMapper;
    @Resource
    private ViewHistoryMapper viewHistoryMapper;

    @Override
    public PageVO<TravelNote> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<TravelNote> page = new PageVO();
        List<TravelNote> list = travelNoteMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        list.forEach(travelNote -> {
            //发布人信息
            User user = userService.selectById(travelNote.getUserId());
            travelNote.setUser(user);
            //评论数
            Integer commentCount = commentInfoMapper.queryCommentsCount("游记",travelNote.getId());
            travelNote.setCommentsCount(commentCount);
            //浏览数
            Integer viewCount = viewHistoryMapper.queryViewCount("游记",travelNote.getId());
            travelNote.setViewCount(viewCount);
            //点赞数
            Integer likeCount = likesMapper.queryLikeCount("游记",travelNote.getId());
            travelNote.setLikesCount(likeCount);
//            //收藏数
//            Integer favoriteCount = favoriteMapper.queryFavoriteCount("游记",travelNote.getId());
//            travelNote.setFavoriteCount(favoriteCount);
        });
        page.setList(list);
        page.setTotal(travelNoteMapper.queryCount(query));
        return page;
    }

    @Override
    public PageVO<TravelNote> homePage(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        CurrentUserDTO dto =  CurrentUserThreadLocal.getCurrentUser();
        List<ViewHistory> views = viewHistoryMapper.queryViewList("游记",dto.getId());
        List<Integer> intList = views.stream().map(ViewHistory::getAssociationId).distinct().collect(Collectors.toList());
        if (intList.size() > pageSize) {
            query.put("associationIds",intList);
        }
        PageVO<TravelNote> page = new PageVO();
        List<TravelNote> list = travelNoteMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        if (list.size() < pageSize) {
            List<TravelNote> ls =  travelNoteMapper.list();
            // 提取当前列表中的 ID 集合，用于去重
            Set<Integer> existingIds = list.stream()
                    .map(TravelNote::getId)
                    .collect(Collectors.toSet());
            // 计算需要补充的数量
            int needed = pageSize - list.size();
            List<TravelNote> supplement = new ArrayList<>();
            // 遍历全量数据，筛选未重复的元素
            for (TravelNote note : ls) {
                if (supplement.size() >= needed) {
                    break; // 已补充足够数量，提前退出
                }
                if (!existingIds.contains(note.getId())) {
                    supplement.add(note);
                    existingIds.add(note.getId()); // 更新已存在的 ID，避免后续重复
                }
            }
            // 将补充的数据添加到原列表
            list.addAll(supplement);
        }
        list.forEach(travelNote -> {
            //发布人信息
            User user = userService.selectById(travelNote.getUserId());
            travelNote.setUser(user);
            //评论数
            Integer commentCount = commentInfoMapper.queryCommentsCount("游记",travelNote.getId());
            travelNote.setCommentsCount(commentCount);
            //浏览数
            Integer viewCount = viewHistoryMapper.queryViewCount("游记",travelNote.getId());
            travelNote.setViewCount(viewCount);
            //点赞数
            Integer likeCount = likesMapper.queryLikeCount("游记",travelNote.getId());
            travelNote.setLikesCount(likeCount);
//            //收藏数
//            Integer favoriteCount = favoriteMapper.queryFavoriteCount("游记",travelNote.getId());
//            travelNote.setFavoriteCount(favoriteCount);
        });
        page.setList(list);
        page.setTotal(travelNoteMapper.queryCount(query));
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
            Integer commentCount = commentInfoMapper.queryCommentsCount("游记",travelNote.getId());
            travelNote.setCommentsCount(commentCount);
            //浏览数
            Integer viewCount = viewHistoryMapper.queryViewCount("游记",travelNote.getId());
            travelNote.setViewCount(viewCount);
            //点赞数
            Integer likeCount = likesMapper.queryLikeCount("游记",travelNote.getId());
            travelNote.setLikesCount(likeCount);
            int isLiked = likesMapper.queryIsLike("游记",travelNote.getId(),user.getId());
            if (isLiked> 0) {
                travelNote.setLiked(true);
            }else {
                travelNote.setLiked(false);
            }
        }
        return travelNote;
    }
    @Override
    public void  putViewCount(Integer id){
        CurrentUserDTO dto =  CurrentUserThreadLocal.getCurrentUser();
        TravelNote note = travelNoteMapper.selectById(id);
        if (note != null) {
            if (dto.getType().equals("USER")) {
                ViewHistory entity = new ViewHistory();
                entity.setTypeCode("游记");
                entity.setUserId(dto.getId());
                entity.setAssociationId(id);
                viewHistoryMapper.insert(entity);
            }
        }
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
        travelNoteMapper.updateById(entity);
    }
    private void check(TravelNote entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        //删除路线详情
        for (Integer id : ids) {
            //删除评论点赞信息
            List<CommentInfo> commentInfos = commentInfoMapper.queryCommentsList("游记",id);
            List<Integer> commentIds = commentInfos.stream().map(CommentInfo::getId).collect(Collectors.toList());
            if (commentIds != null && commentIds.size() > 0) {
                commentInfoMapper.removeByIds(commentIds);
            }
            List<Likes> likesList = likesMapper.queryLikeList("游记",id);
            List<Integer> likesIds = commentInfos.stream().map(CommentInfo::getId).collect(Collectors.toList());
            if (likesIds != null && likesIds.size() > 0) {
                likesMapper.removeByIds(likesIds);
            }
            //浏览数
            List<ViewHistory> viewList = viewHistoryMapper.queryViewList("游记",id);
            List<Integer> viewIds = viewList.stream().map(ViewHistory::getId).collect(Collectors.toList());
            if (viewIds != null && viewIds.size() > 0) {
                viewHistoryMapper.removeByIds(viewIds);
            }
        }
        travelNoteMapper.removeByIds(ids);
    }
}
