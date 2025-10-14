package com.project.platform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 线路分享信息
 */
@Data
public class RouteDays {
    /**
     * id
     */
    private Integer id;
    /**
     * 线路分享id
     */
    private Integer routeId;

    private Integer dayNumber;

    private String title;

    private String locations;

    /**
     * 描述
     */
    private String description;

    private String spots;

    private String accommodation;

    private String transport;
    /**
     * 排序
     */
    private LocalDateTime createTime;
}
