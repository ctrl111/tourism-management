package com.project.platform.controller;


import com.project.platform.entity.Route;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.project.platform.service.RouteService;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("page")
    public ResponseVO<PageVO<Route>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize){
        PageVO<Route> page = routeService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);
    }

    /**
     * 首页路线推荐列表（基于用户浏览历史）
     */
    @GetMapping("homelist")
    public ResponseVO<PageVO<Route>> homelist(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize){
        PageVO<Route> page = routeService.homePage(query, pageNum, pageSize);
        return ResponseVO.ok(page);
    }

    /**
     * 根据id查询
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<Route> selectById(@PathVariable("id") Integer id) {
        Route entity = routeService.selectById(id);
        return ResponseVO.ok(entity);
    }

    /**
     * 增加浏览量（记录视图历史）
     */
    @GetMapping("putViewCount/{id}")
    public ResponseVO putViewCount(@PathVariable("id") Integer id) {
        routeService.putViewCount(id);
        return ResponseVO.ok();
    }

    /**
     * 新增
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody Route entity) {
        routeService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody Route entity) {
        routeService.updateById(entity);
        return ResponseVO.ok();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("delBatch")
    public ResponseVO delBatch(@RequestBody List<Integer> ids) {
        routeService.removeByIds(ids);
        return ResponseVO.ok();
    }
}
