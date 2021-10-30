package com.looseboxes.spring.ratelimiter.annotation;

import com.looseboxes.spring.ratelimiter.RateLimiter;
import com.looseboxes.spring.ratelimiter.RateLimiterTest;
import com.looseboxes.spring.ratelimiter.rates.Rate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RateLimiterForClassLevelAnnotationTest extends RateLimiterTest {

    protected List<Rate> getLimitsThatWillLeadToException() {
        return Arrays.asList(getDefaultLimit());
    }

    @Override
    public RateLimiter getRateLimiter(List<Rate> limits) {
        Map<String, Rate> rates = new HashMap<>();
        for(int i = 0; i < limits.size(); i++) {
            rates.put(getKey(i), limits.get(i));
        }
        return new RateLimiterForClassLevelAnnotation(rates, getBaseRateSupplier());
    }
}
