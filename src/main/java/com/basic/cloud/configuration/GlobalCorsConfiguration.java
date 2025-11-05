package com.basic.cloud.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * 全局跨域配置
 *
 * @author vains
 */
@Configuration(proxyBeanMethods = false)
public class GlobalCorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许的源（Spring Boot 3 推荐使用 allowedOriginPatterns）
        config.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://127.0.0.1:5173", "https://vains-sofia.github.io"));
        // 允许携带 Cookie
        config.setAllowCredentials(true);
        // 允许的请求头
        config.setAllowedHeaders(List.of("*"));
        // 允许的请求方法
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 暴露的响应头
        config.setExposedHeaders(List.of("Authorization"));
        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
