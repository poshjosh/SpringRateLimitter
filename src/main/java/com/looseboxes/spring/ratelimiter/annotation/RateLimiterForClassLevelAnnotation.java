package com.looseboxes.spring.ratelimiter.annotation;

import com.looseboxes.spring.ratelimiter.*;
import com.looseboxes.spring.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.spring.ratelimiter.rates.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class RateLimiterForClassLevelAnnotation implements RateLimiter<String> {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimiterForClassLevelAnnotation.class);

    private final ConcurrentMap<String, RateLimiter<String>> rateLimiters;

    public RateLimiterForClassLevelAnnotation(Map<String, Rate> rates, RateSupplier rateSupplier) {
        this(Util.createRateLimiters(rates, rateSupplier, new RateExceededExceptionThrower<>()));
    }

    public RateLimiterForClassLevelAnnotation(Map<String, RateLimiter<String>> rateLimiters) {
        this.rateLimiters = new ConcurrentHashMap<>(rateLimiters);
    }

    @Override
    public Rate record(String requestURI) throws RateLimitExceededException {
        LOG.trace("               Rate limiting: {}", requestURI);
        final String requestPath = getClassRequestPath(requestURI);
        final RateLimiter<String> rateLimiter = requestPath == null ? null : rateLimiters.get(requestPath);
        final Rate result = rateLimiter == null ? Rate.NONE : rateLimiter.record(requestPath);
        LOG.trace("Result: {}, for rate limiting: {}", result, requestURI);
        return result;
    }

    private String getClassRequestPath(String requestUri) {
        return rateLimiters.keySet().stream()
                .filter(classRequestPath -> requestUri.startsWith(classRequestPath))
                .limit(1)
                .findFirst()
                .orElse(null);
    }
}
