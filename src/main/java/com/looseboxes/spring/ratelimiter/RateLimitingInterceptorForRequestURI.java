package com.looseboxes.spring.ratelimiter;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RateLimitingInterceptorForRequestURI implements HandlerInterceptor {

    private final RateLimiter<String> [] rateLimiters;

    public RateLimitingInterceptorForRequestURI(RateLimiter<String>... rateLimiters) {
        this.rateLimiters = rateLimiters;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        for(RateLimiter<String> rateLimiter : rateLimiters) {
            rateLimiter.record(request.getRequestURI());
        }
        return true;
    }
}
