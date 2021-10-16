package com.looseboxes.spring.ratelimiter;

import com.looseboxes.spring.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.spring.ratelimiter.rates.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class RateLimiterTest {

    private RateLimiter instance;

    @BeforeEach
    void setUp() {
        instance = getRateLimiter(getDefaultLimits());
    }

    @Test
    void updateRate_firstRateShouldEqualBaseRate() {
        Rate result = instance.record("1");
        assertThat(result).isEqualTo(getBaseRate());
    }

    @Test
    void updateRate_firstRateShouldBeLessThanHigherRate() {
        final String key = "1";
        instance.record(key);
        assertThatThrownBy(() -> instance.record(key));
    }

    @Test
    void updateRate_shouldResetWhenAtThreshold() {
        instance = getRateLimiter(getLimitsThatWillLeadToReset());
        final String key = "1";
        Rate result = instance.record(key);
        result = instance.record(key);
        assertThat(result).isEqualTo(getBaseRate());
    }

    @Test
    void updateRate_shouldFailWhenLimitExceeded() {
        instance = getRateLimiter(getLimitsThatWillLeadToException());
        final String key = "1";
        Rate result = instance.record(key);
        assertThatThrownBy(() -> instance.record(key));
    }

    public RateLimiter getRateLimiter(List<Rate> limits) {
        return new RateLimiterImpl(getBaseRateSupplier(), limits);
    }

    private List<Rate> getDefaultLimits() { return Arrays.asList(getDefaultLimit()); }

    private List<Rate> getLimitsThatWillLeadToException() {
        return Arrays.asList(getBaseRate(), getDefaultLimit());
    }

    private Rate getDefaultLimit() {
        return new LimitWithinDuration(1, 3000);
    }

    private List<Rate> getLimitsThatWillLeadToReset() {
        return Arrays.asList(getBaseRate(), getBaseRate());
    }

    private RateSupplier getBaseRateSupplier() {
        return () -> getBaseRate();
    }

    private final LimitWithinDuration baseRate = new LimitWithinDuration();

    private Rate getBaseRate() {
        return baseRate;
    }
}