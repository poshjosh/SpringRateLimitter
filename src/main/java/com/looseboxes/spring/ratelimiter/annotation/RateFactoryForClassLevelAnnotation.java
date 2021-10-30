package com.looseboxes.spring.ratelimiter.annotation;

import com.looseboxes.spring.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.spring.ratelimiter.rates.Rate;

import java.util.*;

public class RateFactoryForClassLevelAnnotation implements RateFromPackageFactory {

    @Override
    public Map<String, Rate> getRates(String controllerPackage) {
        final Map<String, Rate> rates = new HashMap<>();
        try {
            List<Class<?>> controllerClasses = Util.getControllerClasses(controllerPackage);
            for (Class<?> controllerClass : controllerClasses) {
                Util.getRequestMappingOptional(controllerClass)
                        .ifPresent(requestMapping -> {
                            getRateOptional(controllerClass).ifPresent(rate -> {
                                rates.put(requestMapping, rate);
                            });
                        });

            }
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        return rates.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(rates);
    }

    private Optional<Rate> getRateOptional(Class<?> controllerClass){
        final RateLimit rateLimit = controllerClass.getAnnotation(RateLimit.class);
        if(rateLimit != null) {
            return Optional.of(new LimitWithinDuration(rateLimit.limit(), rateLimit.period()));
        }else{
            return Optional.empty();
        }
    }
}
