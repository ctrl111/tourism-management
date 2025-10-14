package com.project.platform.service.impl;

import com.project.platform.entity.Banner;
import com.project.platform.mapper.BannerMapper;
import com.project.platform.service.BannerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 轮播图信息
 */
@Service
public class BannerServiceImpl  implements BannerService {
    @Resource
    private BannerMapper bannerMapper;

    @Override
    public PageVO<Banner> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Banner> page = new PageVO();
        List<Banner> list = bannerMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(bannerMapper.queryCount(query));
        return page;
    }

    @Override
    public Banner selectById(Integer id) {
        Banner banner = bannerMapper.selectById(id);
        return banner;
    }

    @Override
    public List<Banner> list() {
        return bannerMapper.list();
    }
    @Override
    public void insert(Banner entity) {
        check(entity);
        bannerMapper.insert(entity);
    }
    @Override
    public void updateById(Banner entity) {
        check(entity);
        bannerMapper.updateById(entity);
    }
    private void check(Banner entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        bannerMapper.removeByIds(ids);
    }
}
