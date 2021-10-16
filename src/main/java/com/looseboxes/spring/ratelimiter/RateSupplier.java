package com.looseboxes.spring.ratelimiter;

import com.looseboxes.spring.ratelimiter.rates.Rate;

@FunctionalInterface
public interface RateSupplier {

    default Rate getResetRate() {
        return getInitialRate();
    }

    Rate getInitialRate();
}
