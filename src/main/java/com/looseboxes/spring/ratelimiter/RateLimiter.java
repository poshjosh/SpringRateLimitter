package com.looseboxes.spring.ratelimiter;

import com.looseboxes.spring.ratelimiter.rates.Rate;

public interface RateLimiter<K> {
    Rate record(K key) throws RateLimitExceededException;
}
