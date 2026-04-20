package com.project.platform.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户-景点评分DTO
 */
@Data
public class UserScenicScoreDTO {
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 景点ID
     */
    private Integer scenicId;
    
    /**
     * 总评分
     */
    private Double totalScore;
    
    /**
     * 最后操作时间
     */
    private LocalDateTime lastActionTime;
}
