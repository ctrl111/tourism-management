package com.project.platform.service.impl;

import com.project.platform.entity.ScenicCategory;
import com.project.platform.mapper.ScenicCategoryMapper;
import com.project.platform.service.ScenicCategoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 景点分类信息
 */
@Service
public class ScenicCategoryServiceImpl  implements ScenicCategoryService {
    @Resource
    private ScenicCategoryMapper scenicCategoryMapper;

    @Override
    public PageVO<ScenicCategory> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<ScenicCategory> page = new PageVO();
        List<ScenicCategory> list = scenicCategoryMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(scenicCategoryMapper.queryCount(query));
        return page;
    }

    @Override
    public ScenicCategory selectById(Integer id) {
        ScenicCategory scenicCategory = scenicCategoryMapper.selectById(id);
        return scenicCategory;
    }

    @Override
    public List<ScenicCategory> list() {
        return scenicCategoryMapper.list();
    }
    @Override
    public void insert(ScenicCategory entity) {
        check(entity);
        scenicCategoryMapper.insert(entity);
    }
    @Override
    public void updateById(ScenicCategory entity) {
        check(entity);
        scenicCategoryMapper.updateById(entity);
    }
    private void check(ScenicCategory entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        scenicCategoryMapper.removeByIds(ids);
    }
}
