package com.project.platform.controller;

import com.project.platform.entity.TravelNote;
import com.project.platform.service.TravelNoteService;
import com.project.platform.service.UserService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 游记分享信息
 */
@RestController
@RequestMapping("/travelNote")
public class TravelNoteController {
    @Resource
    private TravelNoteService travelNoteService;
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
    public ResponseVO<PageVO<TravelNote>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<TravelNote> page = travelNoteService.page(query, pageNum, pageSize);
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
    public ResponseVO<PageVO<TravelNote>> homePage(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<TravelNote> page = travelNoteService.homePage(query, pageNum, pageSize);
        return ResponseVO.ok(page);

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<TravelNote> selectById(@PathVariable("id") Integer id) {
        TravelNote entity = travelNoteService.selectById(id);
        return ResponseVO.ok(entity);
    }


    /**
     * 列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<TravelNote>> list() {
        return ResponseVO.ok(travelNoteService.list());
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody TravelNote entity) {
        travelNoteService.insert(entity);
        return ResponseVO.ok();
    }
    /**
     * 增加浏览量
     *
     * @param id
     * @return
     */
    @GetMapping("putViewCount/{id}")
    public ResponseVO putViewCount(@PathVariable("id") Integer id) {
        travelNoteService.putViewCount(id);
        return ResponseVO.ok();
    }
    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody TravelNote entity) {
        travelNoteService.updateById(entity);
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
        travelNoteService.removeByIds(ids);
        return ResponseVO.ok();
    }
}
