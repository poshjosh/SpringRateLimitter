package com.looseboxes.spring.ratelimiter.config;

import com.looseboxes.spring.ratelimiter.RateExceededExceptionThrower;
import com.looseboxes.spring.ratelimiter.RateExceededHandler;
import com.looseboxes.spring.ratelimiter.RateLimitingInterceptorForRequestURI;
import com.looseboxes.spring.ratelimiter.annotation.RateLimiterForClassLevelAnnotation;
import com.looseboxes.spring.ratelimiter.annotation.RateLimiterForMethodLevelAnnotation;
import com.looseboxes.spring.ratelimiter.rates.CountWithinDuration;
import com.looseboxes.spring.ratelimiter.rates.Rate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.function.Supplier;

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

            final Supplier<Rate> rateSupplier = () -> new CountWithinDuration();
            final RateExceededHandler<String> rateExceededHandler = new RateExceededExceptionThrower<>();

            RateLimitingInterceptorForRequestURI rateLimitingInterceptor = new RateLimitingInterceptorForRequestURI(
                    new RateLimiterForClassLevelAnnotation(controllerPackage, rateSupplier, rateExceededHandler),
                    new RateLimiterForMethodLevelAnnotation(controllerPackage, rateSupplier, rateExceededHandler)
            );

            registry.addInterceptor(rateLimitingInterceptor);
        }
    }
}
