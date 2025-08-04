package com.oms.inventory.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilterFilterRegistrationBean() {
        FilterRegistrationBean<CorrelationIdFilter> correlationIdFilterRegistrationBean = new FilterRegistrationBean<>();
        correlationIdFilterRegistrationBean.setFilter(new CorrelationIdFilter());
        correlationIdFilterRegistrationBean.addUrlPatterns("/*");
        return correlationIdFilterRegistrationBean;
    }
}
