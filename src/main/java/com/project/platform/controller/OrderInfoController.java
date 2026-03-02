package com.project.platform.controller;

import com.project.platform.dto.BuyTicketDTO;
import com.project.platform.entity.OrderInfo;
import com.project.platform.service.OrderInfoService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单信息
 */
@RestController
@RequestMapping("/orderInfo")
public class OrderInfoController {
    @Resource
    private OrderInfoService orderService;

    /**
     * 分页查询
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public ResponseVO<PageVO<OrderInfo>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<OrderInfo> page = orderService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<OrderInfo> selectById(@PathVariable("id") Integer id) {
        OrderInfo entity = orderService.selectById(id);
        return ResponseVO.ok(entity);
    }


    /**
     * 列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<OrderInfo>> list() {
        return ResponseVO.ok(orderService.list());
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody OrderInfo entity) {
        orderService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody OrderInfo entity) {
        orderService.updateById(entity);
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
        orderService.removeByIds(ids);
        return ResponseVO.ok();
    }

    /**
     * 确认预定
     *
     * @param dto 购票信息
     * @return 订单号
     */
    @PostMapping("confirmBooking")
    public ResponseVO<String> confirmBooking(@RequestBody BuyTicketDTO dto) {
        String orderNo = orderService.buyTickets(dto);
        return ResponseVO.ok(orderNo);
    }
}
