package com.looseboxes.spring.ratelimiter;

import com.looseboxes.spring.ratelimiter.rates.Rate;

public class RateExceededExceptionThrower<K> implements RateExceededHandler<K>{

    @Override
    public void onRateExceeded(K key, Rate rate, Rate limit) {
        throw new RateLimitExceededException(String.format("For: %s, rate: %s exceeds limit: %s", key, rate, limit));
    }
}
