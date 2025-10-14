package com.project.platform.controller;

import com.project.platform.entity.ScenicCategory;
import com.project.platform.service.ScenicCategoryService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 景点分类信息
 */
@RestController
@RequestMapping("/scenicCategory")
public class ScenicCategoryController {
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
    public ResponseVO<PageVO<ScenicCategory>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<ScenicCategory> page = scenicCategoryService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<ScenicCategory> selectById(@PathVariable("id") Integer id) {
        ScenicCategory entity = scenicCategoryService.selectById(id);
        return ResponseVO.ok(entity);
    }


    /**
     * 列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<ScenicCategory>> list() {
        return ResponseVO.ok(scenicCategoryService.list());
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody ScenicCategory entity) {
        scenicCategoryService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody ScenicCategory entity) {
        scenicCategoryService.updateById(entity);
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
        scenicCategoryService.removeByIds(ids);
        return ResponseVO.ok();
    }
}
