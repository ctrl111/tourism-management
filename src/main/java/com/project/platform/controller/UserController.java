package com.project.platform.controller;

import com.project.platform.entity.User;
import com.project.platform.service.UserService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户信息
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户相关操作API")
public class UserController {
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
    @Operation(summary = "分页查询")
    public ResponseVO<PageVO<User>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<User> page = userService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    @Operation(summary = "根据id查询")
    public ResponseVO<User> selectById(@PathVariable("id") Integer id) {
        User entity = userService.selectById(id);
        return ResponseVO.ok(entity);
    }


    /**
     * 列表
     *
     * @return
     */
    @GetMapping("list")
    @Operation(summary = "列表")
    public ResponseVO<List<User>> list() {
        return ResponseVO.ok(userService.list());
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    @Operation(summary = "新增")
    public ResponseVO add(@RequestBody User entity) {
        userService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    @Operation(summary = "更新")
    public ResponseVO update(@RequestBody User entity) {
        userService.updateById(entity);
        return ResponseVO.ok();
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("delBatch")
    @Operation(summary = "批量删除")
    public ResponseVO delBatch(@RequestBody List<Integer> ids) {
        userService.removeByIds(ids);
        return ResponseVO.ok();
    }

    @PostMapping("/topUp/{amount}")
    @Operation(summary = "充值")
    public ResponseVO topUp(@PathVariable BigDecimal amount) {
        Integer userId = CurrentUserThreadLocal.getCurrentUser().getId();
        userService.topUp(userId, amount);
        return ResponseVO.ok();
    }
}
