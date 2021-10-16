package com.looseboxes.spring.ratelimiter.annotation;

import com.looseboxes.spring.ratelimiter.*;
import com.looseboxes.spring.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.spring.ratelimiter.rates.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class RateLimiterForMethodLevelAnnotation implements RateLimiter<String> {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimiterForMethodLevelAnnotation.class);

    private final ConcurrentMap<String, RateLimiter<String>> methodLimiters = new ConcurrentHashMap<>();

    private final RateSupplier rateSupplier;
    private final RateExceededHandler<String> rateExceededHandler;

    public RateLimiterForMethodLevelAnnotation(String controllerPackageName,
                                               RateSupplier rateSupplier,
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

        final String startPath = Util.getRequestMappingOptional(controllerClass).orElse("");

        final Method [] methods = controllerClass.getMethods();

        for (Method method : methods) {

            RateLimit rateLimit = method.getAnnotation(RateLimit.class);

            if (rateLimit != null) {

                final Rate methodRate = new LimitWithinDuration(rateLimit.limit(), rateLimit.period());

                if (method.getAnnotation(GetMapping.class) != null) {
                    String path = startPath + method.getAnnotation(GetMapping.class).path()[0];
                    addMethodLimiter(path, methodRate);
                }

                if (method.getAnnotation(PostMapping.class) != null) {
                    String path = startPath + method.getAnnotation(PostMapping.class).path()[0];
                    addMethodLimiter(path, methodRate);
                }

                if (method.getAnnotation(PutMapping.class) != null) {
                    String path = startPath + method.getAnnotation(PutMapping.class).path()[0];
                    addMethodLimiter(path, methodRate);
                }

                if (method.getAnnotation(DeleteMapping.class) != null) {
                    String path = startPath + method.getAnnotation(DeleteMapping.class).path()[0];
                    addMethodLimiter(path, methodRate);
                }

                if (method.getAnnotation(PatchMapping.class) != null) {
                    String path = method.getAnnotation(PatchMapping.class).path()[0];
                    addMethodLimiter(path, methodRate);
                }

                if (method.getAnnotation(RequestMapping.class) != null) {
                    String path = startPath + method.getAnnotation(RequestMapping.class).path()[0];
                    addMethodLimiter(path, methodRate);
                }
            }
        }
    }

    private void addMethodLimiter(String key, Rate limit) {
        methodLimiters.put(key, getRateLimiter(key, limit));
    }

    private RateLimiter<String> getRateLimiter(String key, Rate limit) {
        return new RateLimiterSingleton<>(rateSupplier, rateExceededHandler, key, limit);
    }

    public Rate record(String requestURI) throws RateLimitExceededException {
        LOG.trace("Rate limiting: {}", requestURI);
        final RateLimiter<String> rateLimiter = methodLimiters.get(requestURI);
        final Rate result = rateLimiter == null ? Rate.NONE : rateLimiter.record(requestURI);
        LOG.trace("Result: {}, for rate limiting: {}", result, requestURI);
        return result;
    }
}
