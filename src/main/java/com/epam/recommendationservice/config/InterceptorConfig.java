package com.epam.recommendationservice.config;

import com.epam.recommendationservice.config.ratelimit.RateLimitInterceptor;
import com.epam.recommendationservice.web.URLs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor interceptor;

    @Autowired
    public InterceptorConfig(RateLimitInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns(URLs.V_1 + "**");
    }
}
