package com.looseboxes.spring.ratelimiter.config;

import com.looseboxes.spring.ratelimiter.rates.Rate;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "rate-limiter", ignoreUnknownFields = false)
public class RateLimitProperties {

    private String controllerPackage;

    private Boolean disabled;

    private Map<String, RateLimitConfig> rateLimits;

    public List<Rate> toRateList() {
        final List<Rate> rateList;
        if(Boolean.TRUE.equals(disabled)) {
            rateList = Collections.emptyList();
        }else if(rateLimits == null || rateLimits.isEmpty()) {
                rateList = Collections.emptyList();
        }else {
            List<Rate> temp = new ArrayList<>(rateLimits.size());
            rateLimits.forEach((name, rateLimitConfig) -> {
                temp.add(rateLimitConfig.toRate());
            });
            rateList = Collections.unmodifiableList(temp);
        }
        return rateList;
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public void setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Map<String, RateLimitConfig> getRateLimits() {
        return rateLimits;
    }

    public void setRateLimits(Map<String, RateLimitConfig> rateLimits) {
        this.rateLimits = rateLimits;
    }

    @Override
    public String toString() {
        return "RateLimitProperties{" +
                "controllerPackage='" + controllerPackage + '\'' +
                ", disabled=" + disabled +
                ", rateLimits=" + rateLimits +
                '}';
    }
}
