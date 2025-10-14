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
        if (CurrentUserThreadLocal.getCurrentUser().getType().equals("USER")) {
            query.put("userId", CurrentUserThreadLocal.getCurrentUser().getId());
        }
        Boolean isParent;
        if (query.get("parentStatus") != null) {
            isParent = true;
        } else {
            isParent = false;
        }
        String typeCode = query.get("typeCode").toString();
        String associationId = query.get("associationId").toString();
        List<CommentInfo> list = commentInfoMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        //简单嵌套循环查询层级数据
        list.forEach(commentInfo -> {
            //发布人信息
            User user = userService.selectById(commentInfo.getUserId());
            commentInfo.setUser(user);
            if (isParent) {
                List<CommentInfo> commentsChildList = commentInfoMapper.queryCommentsListByChild(typeCode, Integer.valueOf(associationId), commentInfo.getId());
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
            throw new CustomException("普通用户才允许添加");
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
        commentInfoMapper.removeByIds(ids);
    }
}
