package com.project.platform.controller;

//import com.project.platform.dto.BuyTicketDTO;
import com.project.platform.entity.ScenicCategory;
import com.project.platform.entity.ScenicInfo;
import com.project.platform.entity.User;
import com.project.platform.service.ScenicCategoryService;
import com.project.platform.service.ScenicInfoService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 景点信息
 */
@RestController
@RequestMapping("/scenicInfo")
public class ScenicInfoController {
    @Resource
    private ScenicInfoService scenicInfoService;
    @Resource
    private ScenicCategoryService scenicCategoryService;

    /**
     * 分页查询
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public ResponseVO<PageVO<ScenicInfo>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        if (query.get("categoryType") != null&& !query.get("categoryType").toString().equals("")) {
            String categoryType = query.get("categoryType").toString();
            query.put("categoryType",categoryType);
        }
        PageVO<ScenicInfo> page = scenicInfoService.page(query, pageNum, pageSize);
        page.getList().forEach(scenicInfo -> {
            ScenicCategory scenicCategory = scenicCategoryService.selectById(scenicInfo.getCategoryId());
            if (scenicCategory != null) {
                scenicInfo.setCategoryType(scenicCategory.getName());
            }
        });
        return ResponseVO.ok(page);

    }

    /**
     * 首页根据用户画像推荐查询数据
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("homelist")
    public ResponseVO<PageVO<ScenicInfo>> homePage(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        if (query.get("categoryType") != null&& !query.get("categoryType").toString().equals("")) {
            String categoryType = query.get("categoryType").toString();
            query.put("categoryType",categoryType);
        }
        PageVO<ScenicInfo> page = scenicInfoService.homePage(query, pageNum, pageSize);
        page.getList().forEach(scenicInfo -> {
            ScenicCategory scenicCategory = scenicCategoryService.selectById(scenicInfo.getCategoryId());
            if (scenicCategory != null) {
                scenicInfo.setCategoryType(scenicCategory.getName());
            }
        });
        return ResponseVO.ok(page);

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<ScenicInfo> selectById(@PathVariable("id") Integer id) {
        ScenicInfo entity = scenicInfoService.selectById(id);
        return ResponseVO.ok(entity);
    }

    /**
     * 增加浏览量
     *
     * @param id
     * @return
     */
    @GetMapping("putViewCount/{id}")
    public ResponseVO putViewCount(@PathVariable("id") Integer id) {
        scenicInfoService.putViewCount(id);
        return ResponseVO.ok();
    }


    /**
     * 列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<ScenicInfo>> list() {
        return ResponseVO.ok(scenicInfoService.list());
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody ScenicInfo entity) {
        scenicInfoService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody ScenicInfo entity) {
        scenicInfoService.updateById(entity);
        return ResponseVO.ok();
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("delBatch")
    public ResponseVO delBatch(@RequestBody List<Integer> ids) {
        scenicInfoService.removeByIds(ids);
        return ResponseVO.ok();
    }

}
