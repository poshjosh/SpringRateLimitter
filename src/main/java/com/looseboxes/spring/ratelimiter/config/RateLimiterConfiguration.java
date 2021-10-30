package com.looseboxes.spring.ratelimiter.config;

import com.looseboxes.spring.ratelimiter.*;
import com.looseboxes.spring.ratelimiter.annotation.*;
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

            RateLimitingInterceptorForRequestURI rateLimitingInterceptor = new RateLimitingInterceptorForRequestURI(
                    classLevelRateLimiter(rateSupplier), methodLevelRateLimiter(rateSupplier)
            );

            registry.addInterceptor(rateLimitingInterceptor);
        }
    }

    RateLimiter<String> classLevelRateLimiter(RateSupplier rateSupplier) {
        return new RateLimiterForClassLevelAnnotation(
                rateFactoryForClassLevelAnnotation().getRates(controllerPackage), rateSupplier);
    }

    RateLimiter<String> methodLevelRateLimiter(RateSupplier rateSupplier) {
        return new RateLimiterForMethodLevelAnnotation(
            rateFactoryForMethodLevelAnnotation().getRates(controllerPackage), rateSupplier);
    }

    RateFactoryForClassLevelAnnotation rateFactoryForClassLevelAnnotation() {
        return new RateFactoryForClassLevelAnnotation();
    }

    RateFactoryForMethodLevelAnnotation rateFactoryForMethodLevelAnnotation() {
        return new RateFactoryForMethodLevelAnnotation();
    }
}
