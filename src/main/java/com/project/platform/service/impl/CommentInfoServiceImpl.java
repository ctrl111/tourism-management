package com.project.platform.service.impl;

import com.project.platform.entity.CommentInfo;
import com.project.platform.entity.User;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.CommentInfoMapper;
import com.project.platform.service.CommentInfoService;
import com.project.platform.service.UserService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 评论记录
 */
@Service
public class CommentInfoServiceImpl implements CommentInfoService {
    @Resource
    private CommentInfoMapper commentInfoMapper;
    @Resource
    private UserService userService;

    @Override
    public PageVO<CommentInfo> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<CommentInfo> page = new PageVO();
        // 只有已登录的普通用户才过滤个人评论，游客和管理员查看所有评论
        if (CurrentUserThreadLocal.getCurrentUser() != null 
            && CurrentUserThreadLocal.getCurrentUser().getType() != null
            && CurrentUserThreadLocal.getCurrentUser().getType().equals("USER")) {
            query.put("userId", CurrentUserThreadLocal.getCurrentUser().getId());
        }
        
        Boolean isParent = query.get("parentStatus") != null;
        
        // 安全获取参数，避免 NullPointerException
        String typeCode = query.get("typeCode") != null ? query.get("typeCode").toString() : null;
        String associationId = query.get("associationId") != null ? query.get("associationId").toString() : null;
        
        List<CommentInfo> list = commentInfoMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        
        //简单嵌套循环查询层级数据
        list.forEach(commentInfo -> {
            //发布人信息
            User user = userService.selectById(commentInfo.getUserId());
            commentInfo.setUser(user);
            
            // 只有在有 typeCode 和 associationId 且需要查询父评论时才查询子评论
            if (isParent && typeCode != null && associationId != null) {
                List<CommentInfo> commentsChildList = commentInfoMapper.queryCommentsListByChild(
                    typeCode, 
                    Integer.valueOf(associationId), 
                    commentInfo.getId()
                );
                if (commentsChildList.size() > 0) {
                    for (CommentInfo commentsChild : commentsChildList) {
                        User userChild = userService.selectById(commentsChild.getUserId());
                        commentsChild.setUser(userChild);
                    }
                    commentInfo.setChildList(commentsChildList);
                }
            }
        });
        
        page.setList(list);
        page.setTotal(commentInfoMapper.queryCount(query));
        return page;
    }

    @Override
    public CommentInfo selectById(Integer id) {
        CommentInfo comments = commentInfoMapper.selectById(id);
        return comments;
    }

    @Override
    public List<CommentInfo> list() {
        return commentInfoMapper.list();
    }

    @Override
    public void insert(CommentInfo entity) {
        if (!CurrentUserThreadLocal.getCurrentUser().getType().equals("USER")) {
            throw new CustomException("Только пользователи могут добавлять комментарии");
        }
        entity.setUserId(CurrentUserThreadLocal.getCurrentUser().getId());
        check(entity);
        commentInfoMapper.insert(entity);
    }

    @Override
    public void updateById(CommentInfo entity) {
        check(entity);
        commentInfoMapper.updateById(entity);
    }

    private void check(CommentInfo entity) {

    }

    @Override
    public void removeByIds(List<Integer> ids) {
        // 对每个要删除的评论ID，先删除其所有子评论
        for (Integer id : ids) {
            // 查找所有以该评论为父评论的子评论
            List<CommentInfo> childComments = commentInfoMapper.queryCommentsByParentId(id);
            if (childComments != null && !childComments.isEmpty()) {
                List<Integer> childIds = childComments.stream()
                        .map(CommentInfo::getId)
                        .collect(java.util.stream.Collectors.toList());
                // 递归删除子评论（如果子评论还有子评论）
                removeByIds(childIds);
            }
        }
        // 删除主评论
        commentInfoMapper.removeByIds(ids);
    }

    @Override
    public Map<String, Object> getStatistics() {
        return commentInfoMapper.getStatistics();
    }
}
