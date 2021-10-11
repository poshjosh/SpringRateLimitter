package com.looseboxes.spring.ratelimiter;

import com.looseboxes.spring.ratelimiter.rates.Rate;

public interface RateLimiter<K> {

    RateLimiter NO_OP = key -> Rate.NONE;

    Rate record(K key) throws RateLimitExceededException;
}
