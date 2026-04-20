package com.project.platform.controller;

import com.project.platform.entity.User;
import com.project.platform.service.UserService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员管理（使用User实体，role=ADMIN）
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private UserService userService;

    /**
     * 分页查询管理员
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public ResponseVO<PageVO<User>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        // 添加role=ADMIN过滤条件
        Map<String, Object> adminQuery = new HashMap<>(query);
        adminQuery.put("role", "ADMIN");
        
        PageVO<User> page = userService.page(adminQuery, pageNum, pageSize);
        return ResponseVO.ok(page);
    }

    /**
     * 根据id查询管理员
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<User> selectById(@PathVariable("id") Integer id) {
        User entity = userService.selectById(id);
        // 验证是否为管理员
        if (entity != null && !"ADMIN".equals(entity.getRole())) {
            return ResponseVO.fail(400, "Этот пользователь не является администратором");
        }
        return ResponseVO.ok(entity);
    }

    /**
     * 管理员列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<User>> list() {
        List<User> allUsers = userService.list();
        // 只返回管理员
        List<User> admins = allUsers.stream()
                .filter(user -> "ADMIN".equals(user.getRole()))
                .collect(Collectors.toList());
        return ResponseVO.ok(admins);
    }

    /**
     * 新增管理员
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody User entity) {
        // 设置角色为管理员
        entity.setRole("ADMIN");
        userService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新管理员
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody User entity) {
        // 确保角色为管理员
        entity.setRole("ADMIN");
        userService.updateById(entity);
        return ResponseVO.ok();
    }

    /**
     * 批量删除管理员
     *
     * @param ids
     * @return
     */
    @DeleteMapping("delBatch")
    public ResponseVO delBatch(@RequestBody List<Integer> ids) {
        userService.removeByIds(ids);
        return ResponseVO.ok();
    }
}
