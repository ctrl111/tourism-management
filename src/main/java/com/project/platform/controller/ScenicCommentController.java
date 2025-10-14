package com.project.platform.controller;

import com.project.platform.entity.ScenicCategory;
import com.project.platform.entity.ScenicComment;
import com.project.platform.entity.ScenicInfo;
import com.project.platform.entity.User;
import com.project.platform.service.ScenicCommentService;
import com.project.platform.service.ScenicInfoService;
import com.project.platform.service.UserService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 景点评论信息
 */
@RestController
@RequestMapping("/scenicComment")
public class ScenicCommentController {
    @Resource
    private ScenicCommentService scenicCommentService;
    @Resource
    private ScenicInfoService scenicInfoService;
    @Resource
    private UserService userService;

    /**
     * 分页查询
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public ResponseVO<PageVO<ScenicComment>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<ScenicComment> page = scenicCommentService.page(query, pageNum, pageSize);
        page.getList().forEach(comment -> {
            ScenicInfo scenicInfo = scenicInfoService.selectById(comment.getScenicId());
            User user = userService.selectById(comment.getUserId());
            if (scenicInfo != null) {
                comment.setScenicName(scenicInfo.getName());
            }
            if (user != null) {
                comment.setUsername(user.getUsername());
                comment.setUser(user);
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
    public ResponseVO<ScenicComment> selectById(@PathVariable("id") Integer id) {
        ScenicComment entity = scenicCommentService.selectById(id);
        return ResponseVO.ok(entity);
    }


    /**
     * 列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<ScenicComment>> list() {
        return ResponseVO.ok(scenicCommentService.list());
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody ScenicComment entity) {
        scenicCommentService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody ScenicComment entity) {
        scenicCommentService.updateById(entity);
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
        scenicCommentService.removeByIds(ids);
        return ResponseVO.ok();
    }

    @GetMapping("selectByScenicId/{scenicId}")
    public ResponseVO<List<ScenicComment>> selectByForumId(@PathVariable("forumId") Integer forumId) {
        List<ScenicComment> list = scenicCommentService.queryCommentsList(forumId);
        return ResponseVO.ok(list);
    }
}
