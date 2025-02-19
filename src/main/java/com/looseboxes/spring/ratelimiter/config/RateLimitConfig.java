package com.looseboxes.spring.ratelimiter.config;

import com.looseboxes.spring.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.spring.ratelimiter.rates.Rate;

import java.util.concurrent.TimeUnit;

public class RateLimitConfig {

    private int limit;
    private long duration;
    private TimeUnit timeUnit;

    public Rate toRate() {
        return new LimitWithinDuration(limit, timeUnit.toMillis(duration));
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Override
    public String toString() {
        return "RateLimitConfig{" +
                "duration=" + duration +
                ", limit=" + limit +
                ", timeUnit=" + timeUnit +
                '}';
    }
}
