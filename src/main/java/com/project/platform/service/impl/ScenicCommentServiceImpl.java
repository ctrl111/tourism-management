package com.project.platform.service.impl;

import com.project.platform.entity.ScenicComment;
import com.project.platform.entity.User;
import com.project.platform.mapper.ScenicCommentMapper;
import com.project.platform.service.ScenicCommentService;
import com.project.platform.service.UserService;
import com.project.platform.utils.CurrentUserThreadLocal;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 景点评论信息
 */
@Service
public class ScenicCommentServiceImpl  implements ScenicCommentService {
    @Resource
    private ScenicCommentMapper scenicCommentMapper;
    @Resource
    private UserService userService;

    @Override
    public PageVO<ScenicComment> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<ScenicComment> page = new PageVO();
        List<ScenicComment> list = scenicCommentMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(scenicCommentMapper.queryCount(query));
        return page;
    }

    @Override
    public ScenicComment selectById(Integer id) {
        ScenicComment scenicComment = scenicCommentMapper.selectById(id);
        return scenicComment;
    }

    @Override
    public List<ScenicComment> list() {
        return scenicCommentMapper.list();
    }
    @Override
    public void insert(ScenicComment entity) {
        // 只有已登录的普通用户才能添加评论
        if(CurrentUserThreadLocal.getCurrentUser() != null 
            && CurrentUserThreadLocal.getCurrentUser().getType() != null
            && CurrentUserThreadLocal.getCurrentUser().getType().equals("USER")){
            entity.setUserId(CurrentUserThreadLocal.getCurrentUser().getId());
        }
        check(entity);
        scenicCommentMapper.insert(entity);
    }
    @Override
    public void updateById(ScenicComment entity) {
        check(entity);
        scenicCommentMapper.updateById(entity);
    }
    private void check(ScenicComment entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        scenicCommentMapper.removeByIds(ids);
    }

    @Override
    public List<ScenicComment> queryCommentsList(Integer id){
        List<ScenicComment> commentsList=  scenicCommentMapper.queryCommentsListByTop(id);
        //简单嵌套循环查询层级数据
        for (ScenicComment comments : commentsList) {
            User user = userService.selectById(comments.getUserId());
            comments.setUser(user);
            List<ScenicComment> commentsChildList = scenicCommentMapper.queryCommentsListByChild(id,comments.getId());
            if(commentsChildList.size()>0){
                for (ScenicComment commentsChild : commentsChildList) {
                    User userChild = userService.selectById(commentsChild.getUserId());
                    commentsChild.setUser(userChild);
                }
                comments.setChildList(commentsChildList);
            }
        }
        return commentsList;
    }
}
