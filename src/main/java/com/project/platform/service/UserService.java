package com.project.platform.service;

import com.project.platform.entity.User;
import com.project.platform.vo.PageVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户信息
 */
public interface UserService extends CommonService{

    void topUp(Integer userId, BigDecimal amount);

    void consumption(Integer userId, BigDecimal amount);

    PageVO<User> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    User selectById(Integer id);

    List<User> list();

    void insert(User entity);

    void updateById(User entity);

    void removeByIds(List<Integer> id);
}
