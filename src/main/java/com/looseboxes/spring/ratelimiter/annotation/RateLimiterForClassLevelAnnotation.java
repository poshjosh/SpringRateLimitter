package com.looseboxes.spring.ratelimiter.annotation;

import com.looseboxes.spring.ratelimiter.RateExceededHandler;
import com.looseboxes.spring.ratelimiter.RateLimitExceededException;
import com.looseboxes.spring.ratelimiter.RateLimiter;
import com.looseboxes.spring.ratelimiter.RateLimiterSingleton;
import com.looseboxes.spring.ratelimiter.rates.CountWithinDuration;
import com.looseboxes.spring.ratelimiter.rates.Rate;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class RateLimiterForClassLevelAnnotation implements RateLimiter<String> {

    private final ConcurrentMap<String, RateLimiter<String>> rateLimiters = new ConcurrentHashMap<>();

    private final Supplier<Rate> rateSupplier;
    private final RateExceededHandler<String> rateExceededHandler;

    public RateLimiterForClassLevelAnnotation(String controllerPackageName,
                                              Supplier<Rate> rateSupplier,
                                              RateExceededHandler<String> rateExceededHandler) {
        this.rateSupplier = rateSupplier;
        this.rateExceededHandler = rateExceededHandler;
        this.init(controllerPackageName);
    }

    private void init(String controllerPackage) {
        try {
            List<Class<?>> controllerClasses = Util.getControllerClasses(controllerPackage);
            for (Class<?> controllerClass : controllerClasses) {
                initController(controllerClass);
            }
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void initController(Class<?> controllerClass){
        Util.getRequestMappingOptional(controllerClass)
                .ifPresent(requestMapping -> {
                    final RateLimit rateLimit = controllerClass.getAnnotation(RateLimit.class);
                    if(rateLimit != null) {
                        final Rate limit = new CountWithinDuration(rateLimit.limit(), rateLimit.period());
                        rateLimiters.put(requestMapping, newRateLimiter(requestMapping, limit));
                    }
                });
    }

    private RateLimiter<String> newRateLimiter(String key, Rate limit) {
        return new RateLimiterSingleton<>(rateSupplier, rateExceededHandler, key, limit);
    }

    @Override
    public Rate record(String requestURI) throws RateLimitExceededException {
        final String requestPath = getClassRequestPath(requestURI);
        final RateLimiter<String> rateLimiter = requestPath == null ? null : rateLimiters.get(requestPath);
        return rateLimiter == null ? null : rateLimiter.record(requestPath);
    }

    private String getClassRequestPath(String requestUri) {
        return rateLimiters.keySet().stream()
                .filter(classRequestPath -> requestUri.startsWith(classRequestPath))
                .limit(1)
                .findFirst()
                .orElse(null);
    }
}
