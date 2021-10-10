package com.looseboxes.spring.ratelimiter.rates;

import com.looseboxes.spring.ratelimiter.annotation.RateLimitProcessor;

import java.io.Serializable;

public final class CountWithinDuration implements Rate, Serializable {

    private final int count;
    private final long duration;
    private final long timeCreated;

    public CountWithinDuration() {
        this(1, 0);
    }

    public CountWithinDuration(int count, long duration) {
        final String limitError = RateLimitProcessor.getErrorMessageIfInvalidLimit(count, null);
        if(limitError != null) {
            throw new IllegalArgumentException(limitError);
        }
        final String periodError = RateLimitProcessor.getErrorMessageIfInvalidPeriod(duration, null);
        if(periodError != null) {
            throw new IllegalArgumentException(periodError);
        }
        this.count = count;
        this.duration = duration;
        this.timeCreated = System.currentTimeMillis();
    }

    @Override
    public int compareTo(Rate other) {
        CountWithinDuration countWithinDuration = (CountWithinDuration) other;
        final int nextCount = incrementCount();
        final long nextDuration = incrementDuration();
        if(nextCount > countWithinDuration.count) {
            if(nextDuration > countWithinDuration.duration) {
                return 0;
            }else{
                return -1;
            }
        }else{
            return 1;
        }
    }

    @Override
    public Rate increment() {
        return new CountWithinDuration(incrementCount(), incrementDuration());
    }

    private int incrementCount() {
        return count + 1;
    }

    private long incrementDuration() {
        return duration + (System.currentTimeMillis() - timeCreated);
    }

    public int getCount() {
        return count;
    }

    public long getDuration() {
        return duration;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    @Override
    public String toString() {
        return "CountWithinDuration{" +
                "count=" + count +
                ", duration=" + duration +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
