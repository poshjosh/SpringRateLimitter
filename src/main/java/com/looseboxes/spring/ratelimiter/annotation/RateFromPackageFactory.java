package com.looseboxes.spring.ratelimiter.annotation;

import com.looseboxes.spring.ratelimiter.rates.Rate;

import java.util.Map;

public interface RateFromPackageFactory {
    Map<String, Rate> getRates(String controllerPackage);
}
