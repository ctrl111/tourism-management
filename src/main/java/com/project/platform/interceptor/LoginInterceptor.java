package com.project.platform.interceptor;

import com.alibaba.fastjson2.JSON;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.exception.CustomException;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("requestStartTime", startTime);
        // OPTIONS请求不做校验,
        // 前后端分离的架构, 前端会发一个OPTIONS请求先做预检, 对预检请求不做校验
        if (request.getMethod().toUpperCase().equals("OPTIONS")) {
            return true;
        }
        String path = request.getRequestURL().toString();
        log.info("接口登录拦截：，path：{}", path);
        //获取header的token参数
        String token = request.getHeader("token");
        log.info("登录校验开始，token：{}", token);
        
        // 检查是否是可选登录的路径（公开接口但支持登录状态）
        boolean isOptionalAuth = isOptionalAuthPath(request.getRequestURI());
        
        if (token == null || token.isEmpty()) {
            if (isOptionalAuth) {
                log.info("可选登录接口，token为空，允许访问但不设置用户信息");
                return true;
            }
            log.info("token为空，请求被拦截");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        Claims claims = JwtUtils.verifyJwt(token);
        //获取用户ID
        if (claims == null) {
            if (isOptionalAuth) {
                log.info("可选登录接口，token无效，允许访问但不设置用户信息");
                return true;
            }
            log.warn("token无效，请求被拦截");
            throw new CustomException(HttpStatus.UNAUTHORIZED,"Недействительный токен");
        } else {
            CurrentUserDTO currentUserDTO = JSON.parseObject(claims.get("currentUser").toString(), CurrentUserDTO.class);
            CurrentUserThreadLocal.set(currentUserDTO);
            log.info("用户信息已设置到ThreadLocal: userId={}", currentUserDTO.getId());
            return true;
        }
    }
    
    /**
     * 判断是否是可选登录的路径
     * 这些路径允许未登录访问，但如果登录了会提供额外功能（如收藏状态）
     */
    private boolean isOptionalAuthPath(String path) {
        return path.startsWith("/scenicInfo/selectById/") 
            || path.startsWith("/travelNote/selectById/")
            || path.startsWith("/scenicInfo/putViewCount/")
            || path.startsWith("/travelNote/putViewCount/")
            || path.equals("/commentsInfo/page");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("requestStartTime");
        log.info("------------- LoginInterceptor 结束 耗时：{} ms -------------", System.currentTimeMillis() - startTime);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CurrentUserThreadLocal.clear();
        log.info("LogInterceptor 结束");
    }
}
