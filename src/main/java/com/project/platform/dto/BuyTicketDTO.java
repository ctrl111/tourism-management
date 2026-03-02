package com.project.platform.dto;

import lombok.Data;

/**
 * 购票DTO
 */
@Data
public class BuyTicketDTO {
    /**
     * 景点ID
     */
    private Integer id;
    
    /**
     * 游玩日期
     */
    private String visitDate;
    
    /**
     * 购买数量
     */
    private Integer number;
    
    /**
     * 总价
     */
    private Double totalPrice;
}
