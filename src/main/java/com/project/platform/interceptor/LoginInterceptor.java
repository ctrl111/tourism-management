package com.project.platform.interceptor;

import com.alibaba.fastjson2.JSON;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.exception.CustomException;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().toUpperCase().equals("OPTIONS")) {
            return true;
        }
        
        String token = request.getHeader("token");
        boolean isOptionalAuth = isOptionalAuthPath(request.getRequestURI());
        
        if (token == null || token.isEmpty()) {
            if (isOptionalAuth) {
                return true;
            }
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        
        Claims claims = JwtUtils.verifyJwt(token);
        if (claims == null) {
            if (isOptionalAuth) {
                return true;
            }
            throw new CustomException(HttpStatus.UNAUTHORIZED,"Недействительный токен");
        } else {
            CurrentUserDTO currentUserDTO = JSON.parseObject(claims.get("currentUser").toString(), CurrentUserDTO.class);
            CurrentUserThreadLocal.set(currentUserDTO);
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
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CurrentUserThreadLocal.clear();
    }
}
