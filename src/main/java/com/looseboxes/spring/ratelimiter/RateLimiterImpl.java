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

        Rate next = cache.get(key);
        if(next == null) {
            next = Objects.requireNonNull(rateSupplier.get());
        }else if(!limits.isEmpty()) {
            int resetCount = 0;
            for(Rate limit : limits) {
                final int n = next.compareTo(limit);
                if(n == 0) {
                    ++resetCount;
                }else if(n < 0) {
                    if(firstExceededLimit == null) {
                        firstExceededLimit = limit;
                    }
                }
            }
            if(resetCount == limits.size()) {
                next = Objects.requireNonNull(rateSupplier.get());
//            next = null; // To limit the size of the Map, we remove rather than reset
            }
        }

        log.debug("\nFor: {}, rate: {} exceeds: {}, limit: {}", key, next, firstExceededLimit != null, firstExceededLimit);

        if(next == null) {
            cache.remove(key);
            next = rateSupplier.get();
        }else{
            cache.put(key, next);
        }

        if(firstExceededLimit != null) {
            rateExceededHandler.onRateExceeded(key, next, firstExceededLimit);
        }

        return next;
    }
}
