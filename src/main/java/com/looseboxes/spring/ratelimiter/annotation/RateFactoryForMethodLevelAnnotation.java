package com.looseboxes.spring.ratelimiter.annotation;

import com.looseboxes.spring.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.spring.ratelimiter.rates.Rate;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

public class RateFactoryForMethodLevelAnnotation implements RateFromPackageFactory {

    @Override
    public Map<String, Rate> getRates(String controllerPackage) {
        final Map<String, Rate> rates = new HashMap<>();
        try {
            List<Class<?>> controllerClasses = Util.getControllerClasses(controllerPackage);
            for (Class<?> controllerClass : controllerClasses) {
                addRates(controllerClass, rates);
            }
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        return rates.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(rates);
    }

    private void addRates(Class<?> controllerClass, Map<String, Rate> addTo){

        final String startPath = Util.getRequestMappingOptional(controllerClass).orElse("");

        final Method[] methods = controllerClass.getMethods();

        for (Method method : methods) {

            RateLimit rateLimit = method.getAnnotation(RateLimit.class);

            if (rateLimit != null) {

                final Rate methodRate = new LimitWithinDuration(rateLimit.limit(), rateLimit.period());

                if (method.getAnnotation(GetMapping.class) != null) {
                    String path = startPath + method.getAnnotation(GetMapping.class).path()[0];
                    addTo.put(path, methodRate);
                }

                if (method.getAnnotation(PostMapping.class) != null) {
                    String path = startPath + method.getAnnotation(PostMapping.class).path()[0];
                    addTo.put(path, methodRate);
                }

                if (method.getAnnotation(PutMapping.class) != null) {
                    String path = startPath + method.getAnnotation(PutMapping.class).path()[0];
                    addTo.put(path, methodRate);
                }

                if (method.getAnnotation(DeleteMapping.class) != null) {
                    String path = startPath + method.getAnnotation(DeleteMapping.class).path()[0];
                    addTo.put(path, methodRate);
                }

                if (method.getAnnotation(PatchMapping.class) != null) {
                    String path = method.getAnnotation(PatchMapping.class).path()[0];
                    addTo.put(path, methodRate);
                }

                if (method.getAnnotation(RequestMapping.class) != null) {
                    String path = startPath + method.getAnnotation(RequestMapping.class).path()[0];
                    addTo.put(path, methodRate);
                }
            }
        }
    }
}
