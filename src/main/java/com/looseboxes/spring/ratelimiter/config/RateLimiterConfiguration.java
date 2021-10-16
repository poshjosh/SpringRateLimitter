package com.looseboxes.spring.ratelimiter.config;

import com.looseboxes.spring.ratelimiter.RateExceededExceptionThrower;
import com.looseboxes.spring.ratelimiter.RateExceededHandler;
import com.looseboxes.spring.ratelimiter.RateLimitingInterceptorForRequestURI;
import com.looseboxes.spring.ratelimiter.RateSupplier;
import com.looseboxes.spring.ratelimiter.annotation.RateLimiterForClassLevelAnnotation;
import com.looseboxes.spring.ratelimiter.annotation.RateLimiterForMethodLevelAnnotation;
import com.looseboxes.spring.ratelimiter.rates.LimitWithinDuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class RateLimiterConfiguration extends WebMvcConfigurationSupport {

    private final String controllerPackage;

    public RateLimiterConfiguration(
            @Value("${rate-limiter.controller-package:''}") String controllerPackageName) {
        this.controllerPackage = controllerPackageName;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        if(StringUtils.hasText(controllerPackage)) {

            final RateSupplier rateSupplier = () -> new LimitWithinDuration();
            final RateExceededHandler<String> rateExceededHandler = new RateExceededExceptionThrower<>();

            RateLimitingInterceptorForRequestURI rateLimitingInterceptor = new RateLimitingInterceptorForRequestURI(
                    new RateLimiterForClassLevelAnnotation(controllerPackage, rateSupplier, rateExceededHandler),
                    new RateLimiterForMethodLevelAnnotation(controllerPackage, rateSupplier, rateExceededHandler)
            );

            registry.addInterceptor(rateLimitingInterceptor);
        }
    }
}
