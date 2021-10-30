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

    public RateLimiterSingleton(RateSupplier rateSupplier, Rate limit) {
        this(rateSupplier, new RateExceededExceptionThrower<>(), null, limit);
    }

    public RateLimiterSingleton(RateSupplier rateSupplier,
                                RateExceededHandler<K> rateExceededHandler,
                                K key,
                                Rate limit) {
        this.rateSupplier = Objects.requireNonNull(rateSupplier);
        this.rateExceededHandler = Objects.requireNonNull(rateExceededHandler);
        this.key = key;
        this.limit = Objects.requireNonNull(limit);
    }

    @Override
    public Rate record(K key) throws RateLimitExceededException {

        if(this.key == null || this.key.equals(key)) {

            final Rate next = rate == null ? getInitialRate() : rate.increment();

            final int n = next.compareTo(limit);

            if(LOG.isDebugEnabled()) {
                LOG.debug("\nFor: {}, rate: {} exceeds: {}, limit: {}", key, next, n > 0, limit);
            }

            rate = n == 0 ? null : next;

            if(n > 0) {
                rateExceededHandler.onRateExceeded(key, next, limit);
            }

            return n == 0 ? Rate.NONE : next;

        }else{
            throw new IllegalArgumentException(String.format("Expected: %s, found: %s", this.key, key));
        }
    }

    private Rate getInitialRate() {
        return Objects.requireNonNull(rateSupplier.getInitialRate());
    }
}
