package com.looseboxes.spring.ratelimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RateLimitingInterceptorForRequestURI implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimitingInterceptorForRequestURI.class);

    private final RateLimiter<String> [] rateLimiters;

    public RateLimitingInterceptorForRequestURI(RateLimiter<String>... rateLimiters) {
        this.rateLimiters = rateLimiters;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws RateLimitExceededException {

        final String requestURI = request.getRequestURI();

        LOG.debug("Invoking {} rate limiters for {}", rateLimiters.length, requestURI);

        for(RateLimiter<String> rateLimiter : rateLimiters) {
            rateLimiter.record(requestURI);
        }

        return true;
    }
}
