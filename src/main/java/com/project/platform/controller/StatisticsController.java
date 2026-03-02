package com.project.platform.controller;


import com.project.platform.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计数据接口
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取景点销售TOP10
     */
    @GetMapping("/scenicSalesTop10")
    public ResponseVO<List<Map<String, Object>>> getScenicSalesTop10() {
        // 使用简化后的order_info表（直接包含scenic_id和quantity）
        String sql = "SELECT " +
                "s.name AS scenicName, " +
                "s.id AS scenicId, " +
                "COALESCE(SUM(o.quantity), 0) AS salesCount " +
                "FROM scenic_info s " +
                "LEFT JOIN order_info o ON s.id = o.scenic_id AND o.status != '已取消' " +
                "GROUP BY s.id, s.name " +
                "ORDER BY salesCount DESC " +
                "LIMIT 10";

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return ResponseVO.ok(result);
    }

    /**
     * 获取分类统计（按分类统计景点数量和销售情况）
     */
    @GetMapping("/categoryStatistics")
    public ResponseVO<List<Map<String, Object>>> getCategoryStatistics() {
        // 使用简化后的order_info表
        String sql = "SELECT " +
                "c.name AS categoryName, " +
                "COUNT(DISTINCT s.id) AS scenicCount, " +
                "COALESCE(COUNT(CASE WHEN o.status != '已取消' THEN o.id END), 0) AS orderCount " +
                "FROM scenic_category c " +
                "LEFT JOIN scenic_info s ON c.id = s.category_id " +
                "LEFT JOIN order_info o ON s.id = o.scenic_id " +
                "GROUP BY c.id, c.name " +
                "ORDER BY orderCount DESC";

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return ResponseVO.ok(result);
    }

    /**
     * 获取总体统计数据（用户数、景点数、订单数等）
     */
    @GetMapping("/overview")
    public ResponseVO<Map<String, Object>> getOverview() {
        Map<String, Object> result = new HashMap<>();

        // 用户总数
        String userCountSql = "SELECT COUNT(*) FROM user";
        Integer userCount = jdbcTemplate.queryForObject(userCountSql, Integer.class);
        result.put("userCount", userCount);

        // 景点总数
        String scenicCountSql = "SELECT COUNT(*) FROM scenic_info";
        Integer scenicCount = jdbcTemplate.queryForObject(scenicCountSql, Integer.class);
        result.put("scenicCount", scenicCount);

        // 订单总数（不包括已取消）
        String orderCountSql = "SELECT COUNT(*) FROM order_info WHERE status != '已取消'";
        Integer orderCount = jdbcTemplate.queryForObject(orderCountSql, Integer.class);
        result.put("orderCount", orderCount);

        // 游记总数
        String travelNoteCountSql = "SELECT COUNT(*) FROM travel_note";
        Integer travelNoteCount = jdbcTemplate.queryForObject(travelNoteCountSql, Integer.class);
        result.put("travelNoteCount", travelNoteCount);

        // 总销售额（按实际订单金额计算）
        String totalRevenueSql = "SELECT COALESCE(SUM(total_amount), 0) FROM order_info WHERE status != '已取消'";
        Double totalRevenue = jdbcTemplate.queryForObject(totalRevenueSql, Double.class);
        result.put("totalRevenue", totalRevenue);

        return ResponseVO.ok(result);
    }
}

