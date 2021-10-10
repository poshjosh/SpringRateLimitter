package com.looseboxes.spring.ratelimiter;

import com.looseboxes.spring.ratelimiter.rates.Rate;
import java.util.function.Supplier;

public class RateLimiterSingleton<K> implements RateLimiter<K> {

    private final Supplier<Rate> rateSupplier;
    private final RateExceededHandler<K> rateExceededHandler;
    private final K key;
    private final Rate limit;
    private Rate rate;

    public RateLimiterSingleton(Supplier<Rate> rateSupplier,
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
            rate = rate == null ? rateSupplier.get() : rate.increment();
            final int n = rate.compareTo(limit);
            if(n < 0) {
                rateExceededHandler.onRateExceeded(key, rate, limit);
            }else if(n == 0) {
                rate = rateSupplier.get();
            }
            return rate;
        }else{
            throw new IllegalArgumentException(String.format("Expected: %s, found: %s", this.key, key));
        }
    }
}
