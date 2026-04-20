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
    
    @Resource
    private com.project.platform.mapper.ScenicInfoMapper scenicInfoMapper;

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
        for (Integer id : ids) {
            ScenicCategory category = scenicCategoryMapper.selectById(id);
            if (category == null) {
                continue;
            }
            
            // 检查是否有景点使用该分类
            int scenicCount = scenicInfoMapper.countByCategoryId(id);
            if (scenicCount > 0) {
                throw new com.project.platform.exception.CustomException(
                    String.format("Невозможно удалить категорию '%s', так как %d достопримечательностей используют эту категорию. Пожалуйста, сначала удалите или переместите эти достопримечательности.", 
                    category.getName(), scenicCount)
                );
            }
        }
        scenicCategoryMapper.removeByIds(ids);
    }
}
