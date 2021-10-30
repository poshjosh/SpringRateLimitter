package com.looseboxes.spring.ratelimiter;

import com.looseboxes.spring.ratelimiter.rates.Rate;

@FunctionalInterface
public interface RateSupplier {
    Rate getInitialRate();
}
