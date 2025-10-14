package com.project.platform.controller;


import com.project.platform.entity.Route;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.project.platform.service.RouteService;

import java.util.Map;

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
}
