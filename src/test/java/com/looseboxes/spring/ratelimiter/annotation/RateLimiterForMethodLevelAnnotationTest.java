package com.looseboxes.spring.ratelimiter.annotation;

import com.looseboxes.spring.ratelimiter.RateLimiter;
import com.looseboxes.spring.ratelimiter.RateLimiterTest;
import com.looseboxes.spring.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.spring.ratelimiter.rates.Rate;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class RateLimiterForMethodLevelAnnotationTest extends RateLimiterTest {

    protected List<Rate> getLimitsThatWillLeadToException() {
        return Arrays.asList(getDefaultLimit());
    }

    @Override
    public RateLimiter getRateLimiter(List<Rate> limits) {
        Map<String, Rate> rates = new HashMap<>();
        for(int i = 0; i < limits.size(); i++) {
            rates.put(getKey(i), limits.get(i));
        }
        return new RateLimiterForMethodLevelAnnotation(rates, getBaseRateSupplier());
    }
}
