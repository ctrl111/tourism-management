package com.project.platform.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Route {

    private Integer id;

    private Integer userId;

    private String title;

    private String description;

    private String cover;

    private Integer days;

    private BigDecimal totalCost;

    private Integer viewCount;

    private Integer likesCount;

    private Integer commentsCount;

    private String content;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 关联字段
    private User user;

    private List<RouteDays> daysList;

    private Boolean liked;
}