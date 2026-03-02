package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.ViewHistory;
import com.project.platform.mapper.*;
import com.project.platform.service.ViewHistoryService;
import com.project.platform.utils.CurrentUserThreadLocal;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 浏览历史
 */
@Service
public class ViewHistoryServiceImpl  implements ViewHistoryService {
    @Resource
    private ViewHistoryMapper viewHistoryMapper;
    @Resource
    private ScenicInfoMapper scenicInfoMapper;
    @Resource
    private TravelNoteMapper travelNoteMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public PageVO<ViewHistory> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        CurrentUserDTO userDTO = CurrentUserThreadLocal.getCurrentUser();
        if (userDTO.getType().equals("USER")){
            query.put("userId", userDTO.getId());
        }
        PageVO<ViewHistory> page = new PageVO();
        List<ViewHistory> list = viewHistoryMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        list.forEach(item->{
            String type = item.getTypeCode();
            item.setUser(userMapper.selectById(item.getUserId()));
            if (type.equals("景点")){
                item.setAssociationObject(scenicInfoMapper.selectById(item.getAssociationId()));
            }
            if (type.equals("游记")){
                item.setAssociationObject(travelNoteMapper.selectById(item.getAssociationId()));
            }
        });
        page.setList(list);
        page.setTotal(viewHistoryMapper.queryCount(query));
        return page;
    }

    @Override
    public ViewHistory selectById(Integer id) {
        ViewHistory viewHistory = viewHistoryMapper.selectById(id);
        return viewHistory;
    }

    @Override
    public List<ViewHistory> list() {
        return viewHistoryMapper.list();
    }
    @Override
    public void insert(ViewHistory entity) {
        check(entity);
        viewHistoryMapper.insert(entity);
    }
    @Override
    public void updateById(ViewHistory entity) {
        check(entity);
        viewHistoryMapper.updateById(entity);
    }
    private void check(ViewHistory entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        viewHistoryMapper.removeByIds(ids);
    }
}
