package com.looseboxes.spring.ratelimiter;

import com.looseboxes.spring.ratelimiter.cache.RateCacheSingleton;
import com.looseboxes.spring.ratelimiter.rates.Rate;

import java.util.Collection;

public class RateLimiterSingleton<K> extends RateLimiterImpl<K>{

    public RateLimiterSingleton(RateSupplier rateSupplier, Collection<Rate> limits) {
        this(null, rateSupplier, limits, new RateExceededExceptionThrower<>());
    }

    public RateLimiterSingleton(K key, RateSupplier rateSupplier, Collection<Rate> limits, RateExceededHandler<K> rateExceededHandler) {
        super(new RateCacheSingleton<>(key), rateSupplier, limits, rateExceededHandler);
    }
}
