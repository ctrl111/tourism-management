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
                        "/v3/api-docs",
                        "/api-docs/swagger-config",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/api-docs",
                        "/common/login",
                        "/common/register",
                        "/common/retrievePassword",
                        "/file/**"
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
