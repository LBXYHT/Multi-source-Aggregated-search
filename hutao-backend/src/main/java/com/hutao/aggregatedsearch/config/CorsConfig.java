package com.hutao.aggregatedsearch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 *
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                // 放行的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 放行哪些请求头
                .allowedHeaders("*")
                // 暴露哪些响应头
                .exposedHeaders("*");
    }
}
