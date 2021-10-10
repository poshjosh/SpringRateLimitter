package com.looseboxes.spring.ratelimiter.rates;

import java.io.Serializable;
import java.util.Objects;

public class CountWithinDurationDTO<ID> implements Serializable {

    private ID id;

    private int count;
    private long duration;
    private long timeCreated;

    public CountWithinDurationDTO() {
        this(null, new CountWithinDuration());
    }

    public CountWithinDurationDTO(ID id, CountWithinDuration delegate) {
        this.id = id;
        this.count = delegate.getCount();
        this.duration = delegate.getDuration();
        this.timeCreated = delegate.getTimeCreated();
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountWithinDurationDTO that = (CountWithinDurationDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CountWithinDurationDTO{" +
                "id='" + id + '\'' +
                ", count=" + getCount() +
                ", duration=" + getDuration() +
                ", timeCreated=" + getTimeCreated() +
                '}';
    }
}
