package com.looseboxes.spring.ratelimiter;

import com.looseboxes.spring.ratelimiter.rates.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RateLimiterSingleton<K> implements RateLimiter<K> {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimiterSingleton.class);

    private final RateSupplier rateSupplier;
    private final RateExceededHandler<K> rateExceededHandler;
    private final K key;
    private final Rate limit;
    private Rate rate;

    public RateLimiterSingleton(RateSupplier rateSupplier,
                                RateExceededHandler<K> rateExceededHandler,
                                K key,
                                Rate limit) {
        this.rateSupplier = rateSupplier;
        this.rateExceededHandler = rateExceededHandler;
        this.key = key;
        this.limit = limit;
    }

    @Override
    public Rate record(K key) throws RateLimitExceededException {

        if(this.key.equals(key)) {
            rate = rate == null ? Objects.requireNonNull(rateSupplier.getInitialRate()) : rate.increment();
            final int n = rate.compareTo(limit);
            if(n < 0) {
                rateExceededHandler.onRateExceeded(key, rate, limit);
            }else if(n == 0) {
                rate = Objects.requireNonNull(rateSupplier.getResetRate());
            }

            if(LOG.isDebugEnabled()) {
                LOG.debug("\nFor: {}, rate: {} exceeds: {}, limit: {}", key, rate, n < 0, limit);
            }

            return rate;
        }else{
            throw new IllegalArgumentException(String.format("Expected: %s, found: %s", this.key, key));
        }
    }
}
