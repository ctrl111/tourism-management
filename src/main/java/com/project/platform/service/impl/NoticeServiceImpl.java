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
        if (dto.getType().equals("USER")){
            query.put("userId",dto.getId());
        }
        if (query.get("typeCode") != null){
            String typeCode = query.get("typeCode").toString();
            if (typeCode.equals("全部")){
                query.remove("typeCode");
            }
        }
        List<Notice> list = noticeMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        list.forEach(item->{
            item.setUser(userMapper.selectById(item.getUserId()));
        });
        page.setList(list);
        page.setTotal(noticeMapper.queryCount(query));
        return page;
    }

    @Override
    public Notice selectById(Integer id) {
        Notice notice = noticeMapper.selectById(id);
        return notice;
    }

    @Override
    public List<Notice> list() {
        return noticeMapper.list();
    }
    @Override
    public void insert(Notice entity) {
        if (entity.getUserIds().size()>0){
            entity.getUserIds().forEach(item->{
                Notice notice = new Notice();
                notice.setUserId(item);
                notice.setTypeCode(entity.getTypeCode());
                notice.setTitle(entity.getTitle());
                notice.setContent(entity.getContent());
                notice.setIsRead("未读");
                noticeMapper.insert(notice);
            });
        }else {
            check(entity);
            noticeMapper.insert(entity);
        }
    }

    @Override
    public void updateById(Notice entity) {
        check(entity);
        noticeMapper.updateById(entity);
    }
    private void check(Notice entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        noticeMapper.removeByIds(ids);
    }
}
