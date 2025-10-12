package com.example.petapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.file.base-path}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/profiles/**")
                .addResourceLocations("file:" + basePath + "/profiles/");
        registry.addResourceHandler("/image/posts/**")
                .addResourceLocations("file:" + basePath + "/posts/");
        registry.addResourceHandler("/image/members/**")
                .addResourceLocations("file:" + basePath + "/members/");
        registry.addResourceHandler("/image/basic/**")
                .addResourceLocations("file:" + basePath + "/basic/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API
                .allowedOriginPatterns("*") //
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true);
    }
}
