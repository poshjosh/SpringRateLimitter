package com.looseboxes.spring.ratelimiter;

import com.looseboxes.spring.ratelimiter.cache.RateCache;
import com.looseboxes.spring.ratelimiter.rates.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class RateLimiterImpl<K> implements RateLimiter<K> {

    private final Logger log = LoggerFactory.getLogger(RateLimiterImpl.class);

    private final RateCache<K> cache;

    private final Supplier<Rate> rateSupplier;

    private final List<Rate> limits;

    private final RateExceededHandler<K> rateExceededHandler;

    public RateLimiterImpl(RateCache<K> cache, Supplier<Rate> rateSupplier, List<Rate> limits) {
        this(cache, rateSupplier, limits, new RateExceededExceptionThrower<>());
    }

    public RateLimiterImpl(
            RateCache<K> cache,
            Supplier<Rate> rateSupplier,
            List<Rate> limits,
            RateExceededHandler<K> rateExceededHandler) {
        this.cache = Objects.requireNonNull(cache);
        this.rateSupplier = Objects.requireNonNull(rateSupplier);
        this.limits = Objects.requireNonNull(limits);
        this.rateExceededHandler = Objects.requireNonNull(rateExceededHandler);
    }

    @Override
    public Rate record(K key) throws RateLimitExceededException {

        Rate firstExceededLimit = null;

        Rate rate = cache.get(key);
        rate = rate == null ? Objects.requireNonNull(rateSupplier.get()) : rate.increment();

        if(!limits.isEmpty()) {
            int resetCount = 0;
            for(Rate limit : limits) {
                final int n = rate.compareTo(limit);
                if(n == 0) {
                    ++resetCount;
                }else if(n < 0) {
                    if(firstExceededLimit == null) {
                        firstExceededLimit = limit;
                    }
                }
            }
            if(resetCount == limits.size()) {
                rate = Objects.requireNonNull(rateSupplier.get());
//                rate = Rate.NONE; // To limit the size of the Map, we may remove rather than reset
            }
        }

        log.debug("\nFor: {}, rate: {} exceeds: {}, limit: {}", key, rate, firstExceededLimit != null, firstExceededLimit);

        if(rate == Rate.NONE) {
            cache.remove(key);
        }else{
            cache.put(key, rate);
        }

        if(firstExceededLimit != null) {
            rateExceededHandler.onRateExceeded(key, rate, firstExceededLimit);
        }

        return rate;
    }
}
