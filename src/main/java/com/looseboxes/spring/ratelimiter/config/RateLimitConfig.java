package com.looseboxes.spring.ratelimiter.config;

import com.looseboxes.spring.ratelimiter.rates.CountWithinDuration;
import com.looseboxes.spring.ratelimiter.rates.Rate;

import java.util.concurrent.TimeUnit;

public class RateLimitConfig {

    private int count;
    private long duration;
    private TimeUnit timeUnit;

    public Rate toRate() {
        return new CountWithinDuration(count, timeUnit.toMillis(duration));
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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
                ", count=" + count +
                ", timeUnit=" + timeUnit +
                '}';
    }
}
