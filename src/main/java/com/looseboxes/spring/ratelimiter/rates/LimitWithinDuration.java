package com.looseboxes.spring.ratelimiter.rates;

import com.looseboxes.spring.ratelimiter.annotation.RateLimitProcessor;

import java.io.Serializable;

public final class LimitWithinDuration implements Rate, Serializable {

    public static final LimitWithinDuration NONE = new LimitWithinDuration(0, 0);

    private final int limit;
    private final long duration;
    private final long timeCreated;

    public LimitWithinDuration() {
        this(1, 0);
    }

    public LimitWithinDuration(int limit, long duration) {
        final String limitError = RateLimitProcessor.getErrorMessageIfInvalidLimit(limit, null);
        if(limitError != null) {
            throw new IllegalArgumentException(limitError);
        }
        final String periodError = RateLimitProcessor.getErrorMessageIfInvalidPeriod(duration, null);
        if(periodError != null) {
            throw new IllegalArgumentException(periodError);
        }
        this.limit = limit;
        this.duration = duration;
        this.timeCreated = System.currentTimeMillis();
    }

    @Override
    public int compareTo(Rate other) {
        LimitWithinDuration limitWithinDuration = (LimitWithinDuration) other;
        if(limit > limitWithinDuration.limit) {
            if(duration > limitWithinDuration.duration) {
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
        return new LimitWithinDuration(incrementCount(), incrementDuration());
    }

    private int incrementCount() {
        return limit + 1;
    }

    private long incrementDuration() {
        return duration + (System.currentTimeMillis() - timeCreated);
    }

    public int getLimit() {
        return limit;
    }

    public long getDuration() {
        return duration;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    @Override
    public String toString() {
        return "LimitWithinDuration{" +
                "limit=" + limit +
                ", duration=" + duration +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
