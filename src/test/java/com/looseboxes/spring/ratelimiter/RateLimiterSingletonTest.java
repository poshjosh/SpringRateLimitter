package com.looseboxes.spring.ratelimiter;

import com.looseboxes.spring.ratelimiter.rates.Rate;

import java.util.List;

public class RateLimiterSingletonTest extends RateLimiterTest {

    @Override
    public RateLimiter getRateLimiter(List<Rate> limits) {
        return new RateLimiterSingleton(getBaseRateSupplier(), limits);
    }
}
