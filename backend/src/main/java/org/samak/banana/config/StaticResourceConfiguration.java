package org.samak.banana.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// todo SAMAK is this method usefull ?
@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {


    private static final String[] ROUTER = {"/sprints", "/stories", "/plush"};


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/webjars/**")//
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/img/**")//
                .addResourceLocations("classpath:/img/");

        registry.addResourceHandler("/**")//
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler(ROUTER)//
                .addResourceLocations("classpath:/static/index.html");

    }

}