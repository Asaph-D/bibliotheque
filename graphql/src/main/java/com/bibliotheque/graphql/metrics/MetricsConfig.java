package com.bibliotheque.graphql.metrics;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class MetricsConfig {

    @Bean
    public FilterRegistrationBean<RequestMetricsFilter> requestMetricsFilterRegistration(RequestMetricsFilter filter) {
        FilterRegistrationBean<RequestMetricsFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/graphql");
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }
}

