package com.example.glowtales.config;

import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Controller
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        MustacheViewResolver resolver = new MustacheViewResolver(); //mustache를 재설정할 건데
        resolver.setCharset("UTF-8"); //인코딩은 기본적으노 utf-8이고
        resolver.setContentType("text/html;charset=UTF-8"); //던지는 형식은 html;utf-8이야
        resolver.setPrefix("classpath:/templates/"); //classpath는 우리 프로젝트까지의 경로이고 templates에 존재하며
        resolver.setSuffix(".html"); ///.html도 mustache로 인식하게 됨

        registry.viewResolver(resolver); //그리고 이걸로 Resolver로 재설정
    }
}
