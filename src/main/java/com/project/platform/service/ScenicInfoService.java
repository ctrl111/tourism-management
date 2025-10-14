package com.project.platform.service;

//import com.project.platform.dto.BuyTicketDTO;
import com.project.platform.entity.ScenicInfo;
import com.project.platform.vo.PageVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 景点信息
 */
public interface ScenicInfoService {

    PageVO<ScenicInfo> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    PageVO<ScenicInfo> homePage(Map<String, Object> query, Integer pageNum, Integer pageSize);

    ScenicInfo selectById(Integer id);

    void  putViewCount(Integer id);

    List<ScenicInfo> list();

    void insert(ScenicInfo entity);

    void updateById(ScenicInfo entity);

    void removeByIds(List<Integer> id);

}
