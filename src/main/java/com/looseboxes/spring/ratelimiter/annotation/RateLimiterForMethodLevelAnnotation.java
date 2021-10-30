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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class RateLimiterForMethodLevelAnnotation implements RateLimiter<String> {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimiterForMethodLevelAnnotation.class);

    private final ConcurrentMap<String, RateLimiter<String>> methodLimiters;

    public RateLimiterForMethodLevelAnnotation(Map<String, Rate> rates, RateSupplier rateSupplier) {
        this(Util.createRateLimiters(rates, rateSupplier, new RateExceededExceptionThrower<>()));
    }

    public RateLimiterForMethodLevelAnnotation(Map<String, RateLimiter<String>> methodLimiters) {
        this.methodLimiters = new ConcurrentHashMap<>(methodLimiters);
    }

    public Rate record(String requestURI) throws RateLimitExceededException {
        LOG.trace("Rate limiting: {}", requestURI);
        final RateLimiter<String> rateLimiter = methodLimiters.get(requestURI);
        final Rate result = rateLimiter == null ? Rate.NONE : rateLimiter.record(requestURI);
        LOG.debug("Result: {}, for rate limiting: {}", result, requestURI);
        return result;
    }
}
