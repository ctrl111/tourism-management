package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.Notice;
import com.project.platform.mapper.NoticeMapper;
import com.project.platform.mapper.UserMapper;
import com.project.platform.service.NoticeService;
import com.project.platform.utils.CurrentUserThreadLocal;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 系统通知
 */
@Service
public class NoticeServiceImpl  implements NoticeService {
    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public PageVO<Notice> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Notice> page = new PageVO();
        CurrentUserDTO dto = CurrentUserThreadLocal.getCurrentUser();
        
        // 权限控制：
        // 1. 普通用户（USER）：只能查看自己的通知（所有通知都是私有的）
        // 2. 管理员（ADMIN）：可以查看所有通知（用于管理后台）
        // 3. 游客（未登录）：无法查看任何通知（返回空列表）
        
        if (dto != null && dto.getType() != null) {
            if (dto.getType().equals("USER")) {
                // 普通用户：只查看自己的通知
                query.put("userId", dto.getId());
            } else if (dto.getType().equals("ADMIN")) {
                // 管理员：可以查看所有通知
                query.put("isAdmin", true);
            }
        } else {
            // 游客：返回空列表（userId = -1 用于标记游客，不返回任何数据）
            query.put("userId", -1);
            query.put("isGuest", true);
        }
        
        List<Notice> list = noticeMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        list.forEach(item->{
            if (item.getUserId() != null) {
                item.setUser(userMapper.selectById(item.getUserId()));
            }
        });
        page.setList(list);
        page.setTotal(noticeMapper.queryCount(query));
        return page;
    }

    @Override
    public Notice selectById(Integer id) {
        Notice notice = noticeMapper.selectById(id);
        
        // 权限检查：普通用户只能查看自己的通知
        CurrentUserDTO dto = CurrentUserThreadLocal.getCurrentUser();
        if (dto != null && dto.getType() != null && dto.getType().equals("USER")) {
            if (notice != null && !notice.getUserId().equals(dto.getId())) {
                // 不是该用户的通知，返回 null 或抛出异常
                throw new RuntimeException("У вас нет прав на просмотр этого уведомления");
            }
        }
        
        return notice;
    }

    @Override
    public List<Notice> list() {
        return noticeMapper.list();
    }
    @Override
    public void insert(Notice entity) {
        // 如果指定了用户列表，则为每个用户创建私有通知
        if (entity.getUserIds() != null && entity.getUserIds().size() > 0){
            entity.getUserIds().forEach(item->{
                Notice notice = new Notice();
                notice.setUserId(item);
                notice.setTypeCode(entity.getTypeCode());
                notice.setTitle(entity.getTitle());
                notice.setContent(entity.getContent());
                notice.setIsRead("UNREAD");
                noticeMapper.insert(notice);
            });
        } else {
            // 如果没有指定用户列表，直接插入单条通知
            // 注意：所有通知都必须有 userId，不允许 userId 为 null
            check(entity);
            
            if (entity.getIsRead() == null || entity.getIsRead().isEmpty()) {
                entity.setIsRead("UNREAD");
            }
            
            // 确保 userId 不为空
            if (entity.getUserId() == null) {
                throw new RuntimeException("Уведомление должно иметь ID пользователя");
            }
            
            noticeMapper.insert(entity);
        }
    }

    @Override
    public void updateById(Notice entity) {
        // 权限检查：普通用户只能修改自己的通知
        CurrentUserDTO dto = CurrentUserThreadLocal.getCurrentUser();
        if (dto != null && dto.getType() != null && dto.getType().equals("USER")) {
            Notice existNotice = noticeMapper.selectById(entity.getId());
            if (existNotice != null && !existNotice.getUserId().equals(dto.getId())) {
                throw new RuntimeException("У вас нет прав на изменение этого уведомления");
            }
        }
        
        check(entity);
        noticeMapper.updateById(entity);
    }
    
    private void check(Notice entity) {

    }
    
    @Override
    public void removeByIds(List<Integer> ids) {
        // 权限检查：普通用户只能删除自己的通知
        CurrentUserDTO dto = CurrentUserThreadLocal.getCurrentUser();
        if (dto != null && dto.getType() != null && dto.getType().equals("USER")) {
            for (Integer id : ids) {
                Notice notice = noticeMapper.selectById(id);
                if (notice != null && !notice.getUserId().equals(dto.getId())) {
                    throw new RuntimeException("У вас нет прав на удаление этого уведомления");
                }
            }
        }
        
        noticeMapper.removeByIds(ids);
    }
}
