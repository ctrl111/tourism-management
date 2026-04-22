package com.project.platform.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.dto.RetrievePasswordDTO;
import com.project.platform.dto.UpdatePasswordDTO;
import com.project.platform.entity.User;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.UserMapper;
import com.project.platform.service.FileService;
import com.project.platform.service.UserService;
import com.project.platform.utils.CurrentUserThreadLocal;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户信息
 */
@Service
@Tag(name = "用户管理", description = "用户相关操作API")
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private FileService fileService;
    @Value("${resetPassword}")
    private String resetPassword;

    @Override
    public PageVO<User> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<User> page = new PageVO();
        List<User> list = userMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(userMapper.queryCount(query));
        return page;
    }

    @Override
    public User selectById(Integer id) {
        User user = userMapper.selectById(id);
        return user;
    }

    @Override
    public List<User> list() {
        return userMapper.list();
    }
    @Override
    public void insert(User entity) {
        check(entity);
        userMapper.insert(entity);
    }
    @Override
    public void updateById(User entity) {
        check(entity);
        
        // 如果密码为空或null，则不更新密码（保留原密码）
        if (entity.getPassword() == null || entity.getPassword().trim().isEmpty()) {
            User existingUser = userMapper.selectById(entity.getId());
            entity.setPassword(existingUser.getPassword());
        }
        
        userMapper.updateById(entity);
    }
    private void check(User entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        // 删除用户前，先删除关联的头像文件
        for (Integer id : ids) {
            User user = userMapper.selectById(id);
            if (user != null && StringUtils.isNotBlank(user.getAvatarUrl())) {
                fileService.deleteFileByUrl(user.getAvatarUrl());
            }
        }
        userMapper.removeByIds(ids);
    }

    @Override
    public CurrentUserDTO login(String username, String password) {
        User user = null;
        
        // Убираем + и пробелы из username для проверки
        String cleanUsername = username.replaceAll("[+\\s-]", "");
        
        // 尝试用手机号登录 (российский формат: +7XXXXXXXXXX, 7XXXXXXXXXX или 8XXXXXXXXXX)
        if (cleanUsername.matches("^[78]\\d{10}$")) {
            // Ищем по оригинальному username (с +) и по очищенному
            user = userMapper.selectByPhone(username);
            if (user == null) {
                user = userMapper.selectByPhone(cleanUsername);
            }
        }
        // 尝试用邮箱登录
        else if (username.contains("@")) {
            user = userMapper.selectByEmail(username);
        }
        // 尝试用用户名登录
        else {
            user = userMapper.selectByUsername(username);
        }
        
        if (user == null || !user.getPassword().equals(password)) {
            throw new CustomException("Неверное имя пользователя/телефон/email или пароль");
        }
        
        // 检查用户状态（统一使用 ACTIVE/INACTIVE）
        if ("INACTIVE".equals(user.getStatus())) {
            throw new CustomException("Пользователь заблокирован, обратитесь к администратору");
        }
        
        CurrentUserDTO currentUserDTO = new CurrentUserDTO();
        BeanUtils.copyProperties(user, currentUserDTO);
        // 设置type为角色（ADMIN或USER）
        currentUserDTO.setType(user.getRole());
        return currentUserDTO;
    }

    @Override
    public void register(JSONObject data) {
        String phone = data.getString("phone");
        String email = data.getString("email");
        String password = data.getString("password");
        
        // 验证必填字段
        if (password == null || password.trim().isEmpty()) {
            throw new CustomException("Пароль не может быть пустым");
        }
        
        // 验证密码长度（6-20位）
        if (password.length() < 6 || password.length() > 20) {
            throw new CustomException("Длина пароля должна быть от 6 до 20 символов");
        }
        
        // 验证手机号和邮箱至少填一个
        boolean hasPhone = phone != null && !phone.trim().isEmpty();
        boolean hasEmail = email != null && !email.trim().isEmpty();
        
        if (!hasPhone && !hasEmail) {
            throw new CustomException("Необходимо указать телефон или email");
        }
        
        // 验证手机号格式（如果填写了）- российский формат: +7XXXXXXXXXX, 7XXXXXXXXXX или 8XXXXXXXXXX
        if (hasPhone) {
            // Убираем + и пробелы для валидации
            String cleanPhone = phone.replaceAll("[+\\s-]", "");
            if (!cleanPhone.matches("^[78]\\d{10}$")) {
                throw new CustomException("Неверный формат номера телефона. Используйте формат: +7XXXXXXXXXX, 7XXXXXXXXXX или 8XXXXXXXXXX");
            }
        }
        
        // 验证邮箱格式（如果填写了）
        if (hasEmail && !email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            throw new CustomException("Неверный формат email");
        }
        
        // 检查手机号是否已注册（如果填写了）
        if (hasPhone) {
            User existPhone = userMapper.selectByPhone(phone);
            if (existPhone != null) {
                throw new CustomException("Этот номер телефона уже зарегистрирован");
            }
        }
        
        // 检查邮箱是否已被使用（如果填写了）
        if (hasEmail) {
            User existEmail = userMapper.selectByEmail(email);
            if (existEmail != null) {
                throw new CustomException("Этот email уже используется");
            }
        }
        
        // 创建新用户
        User user = new User();
        user.setPassword(password);
        
        // 设置手机号和邮箱
        if (hasPhone) {
            user.setPhone(phone);
        }
        if (hasEmail) {
            user.setEmail(email);
        }
        
        // 设置初始用户名（优先使用手机号，其次使用邮箱）
        if (hasPhone) {
            user.setUsername(phone);
        } else {
            user.setUsername(email);
        }
        
        // 设置头像（可选）
        String avatarUrl = data.getString("avatarUrl");
        user.setAvatarUrl(avatarUrl);
        
        // 设置默认值
        user.setBalance(new BigDecimal("1000.00")); // 新用户赠送1000元余额
        user.setStatus("ACTIVE"); // 状态：启用
        user.setRole("USER"); // 角色：普通用户（注册只能注册普通用户）
        
        // 保存用户
        insert(user);
    }


    @Override
    public void updateCurrentUserInfo(CurrentUserDTO currentUserDTO) {
        User user = userMapper.selectById(currentUserDTO.getId());
        
        // 如果更换了头像，删除旧头像文件
        if (StringUtils.isNotBlank(currentUserDTO.getAvatarUrl()) 
            && !currentUserDTO.getAvatarUrl().equals(user.getAvatarUrl())
            && StringUtils.isNotBlank(user.getAvatarUrl())) {
            fileService.deleteFileByUrl(user.getAvatarUrl());
        }
        
        user.setId(currentUserDTO.getId());
        user.setUsername(currentUserDTO.getUsername()); // 允许修改用户名
        user.setAvatarUrl(currentUserDTO.getAvatarUrl());
        user.setPhone(currentUserDTO.getPhone());
        user.setEmail(currentUserDTO.getEmail());
        userMapper.updateById(user);
    }

    @Override
    public void updateCurrentUserPassword(UpdatePasswordDTO updatePassword) {
        User user = userMapper.selectById(CurrentUserThreadLocal.getCurrentUser().getId());
        if (!user.getPassword().equals(updatePassword.getOldPassword())) {
            throw new CustomException("Неверный старый пароль");
        }
        user.setPassword(updatePassword.getNewPassword());
        userMapper.updateById(user);
    }

    @Override
    public void resetPassword(Integer id) {
        User user = userMapper.selectById(id);
        user.setPassword(resetPassword);
        userMapper.updateById(user);
    }

    @Override
    public void retrievePassword(RetrievePasswordDTO retrievePasswordDTO) {
        User user = userMapper.selectByPhone(retrievePasswordDTO.getPhone());
        if (user == null) {
            throw new CustomException("Номер телефона не существует");
        }
        //TODO 校验验证码
        user.setPassword(retrievePasswordDTO.getPassword());
        userMapper.updateById(user);
    }


    /**
     * 充值
     *
     * @param userId
     * @param amount
     */

    public void topUp(Integer userId, BigDecimal amount) {
        User user = selectById(userId);
        user.setBalance(user.getBalance().add(amount));
        userMapper.updateById(user);
    }

    /**
     * 消费
     *
     * @param userId
     * @param amount
     */
    public void consumption(Integer userId, BigDecimal amount) {
        User user = selectById(userId);
        user.setBalance(user.getBalance().subtract(amount));
        if (user.getBalance().compareTo(new BigDecimal("0")) < 0) {
            throw new CustomException("Недостаточно средств");
        }
        userMapper.updateById(user);
    }
}
