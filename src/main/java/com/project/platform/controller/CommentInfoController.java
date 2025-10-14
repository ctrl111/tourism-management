package com.project.platform.controller;

import com.project.platform.entity.CommentInfo;
import com.project.platform.service.CommentInfoService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 评论记录
 */
@RestController
@RequestMapping("/commentsInfo")
public class CommentInfoController {
    @Resource
    private CommentInfoService commentInfoService;

    /**
     * 分页查询
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public ResponseVO<PageVO<CommentInfo>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<CommentInfo> page = commentInfoService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<CommentInfo> selectById(@PathVariable("id") Integer id) {
        CommentInfo entity = commentInfoService.selectById(id);
        return ResponseVO.ok(entity);
    }



    /**
     * 列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<CommentInfo>> list() {
        return ResponseVO.ok(commentInfoService.list());
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody CommentInfo entity) {
        commentInfoService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody CommentInfo entity) {
        commentInfoService.updateById(entity);
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
        commentInfoService.removeByIds(ids);
        return ResponseVO.ok();
    }

}
