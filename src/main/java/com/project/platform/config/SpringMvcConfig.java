package com.project.platform.config;

import com.project.platform.interceptor.LoginInterceptor;
import io.swagger.v3.oas.models.ExternalDocumentation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@Slf4j
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    LoginInterceptor loginInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                //允许直接访问的接口
                .excludePathPatterns(
                        // 系统端点
                        "/error",
                        
                        // Swagger 文档
                        "/v3/api-docs",
                        "/api-docs/swagger-config",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/api-docs",
                        
                        // 公共接口（登录、注册等）
                        "/common/login",
                        "/common/register",
                        "/common/retrievePassword",
                        
                        // 文件访问
                        "/file/**",
                        
                        // 首页公开API
                        "/scenicInfo/homelist",
                        "/route/homelist",
                        "/banner/page",
                        "/travelNote/homelist",
                        
                        // 列表页公开API
                        "/scenicInfo/page",
                        "/travelNote/page",
                        "/route/page",
                        
                        // 分类和筛选API
                        "/scenicCategory/list",
                        
                        // 详情页公开API
                        "/scenicInfo/detail/**",
                        "/travelNote/detail/**",
                        "/route/detail/**",
                        "/scenicInfo/selectById/**",
                        "/travelNote/selectById/**",
                        "/route/selectById/**",
                        "/scenicInfo/putViewCount/**",
                        "/travelNote/putViewCount/**",
                        "/route/putViewCount/**",
                        
                        // 评论相关公开API
                        "/commentsInfo/page",
                        "/scenicComment/page"
                );
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API文档")
                        .version("1.0")
                        .description("SpringDoc生成的API文档"))
                .externalDocs(new ExternalDocumentation()
                        .description("更多文档")
                        .url("https://example.com"));
    }

}
