package com.project.platform.controller;

import com.project.platform.entity.Favorite;
import com.project.platform.entity.Likes;
import com.project.platform.service.LikesService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 点赞信息
 */
@RestController
@RequestMapping("/likes")
public class LikesController {
    @Resource
    private LikesService listService;

    /**
     * 分页查询
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public ResponseVO<PageVO<Likes>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<Likes> page = listService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<Likes> selectById(@PathVariable("id") Integer id) {
        Likes entity = listService.selectById(id);
        return ResponseVO.ok(entity);
    }


    /**
     * 列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<Likes>> list() {
        return ResponseVO.ok(listService.list());
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody Likes entity) {
        listService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("del")
    public ResponseVO del(@RequestBody Likes entity) {
        listService.del(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody Likes entity) {
        listService.updateById(entity);
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
        listService.removeByIds(ids);
        return ResponseVO.ok();
    }
}
