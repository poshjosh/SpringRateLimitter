package com.looseboxes.spring.ratelimiter.annotation;

import com.looseboxes.spring.ratelimiter.RateExceededHandler;
import com.looseboxes.spring.ratelimiter.RateLimiter;
import com.looseboxes.spring.ratelimiter.RateLimiterSingleton;
import com.looseboxes.spring.ratelimiter.RateSupplier;
import com.looseboxes.spring.ratelimiter.rates.Rate;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

class Util {

    static Map<String, RateLimiter<String>> createRateLimiters(
            Map<String, Rate> rates,
            RateSupplier rateSupplier,
            RateExceededHandler<String> rateExceededHandler) {
        final Map<String, RateLimiter<String>> rateLimiters;
        if(rates.isEmpty()) {
            rateLimiters = Collections.emptyMap();
        }else{
            rateLimiters = new HashMap<>(rates.size(), 1.0f);
            rates.forEach((path, rate) -> {
                rateLimiters.put(path, new RateLimiterSingleton<>(
                        path, rateSupplier, Collections.singletonList(rate), rateExceededHandler
                ));
            });
        }
        return rateLimiters.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(rateLimiters);
    }

    static List<Class<?>> getControllerClasses(String controllerPackage) throws ClassNotFoundException{

        if(StringUtils.hasText(controllerPackage)) {

            final List<Class<?>> controllerClasses = new ArrayList<>();

            ClassPathScanningCandidateComponentProvider scanner =
                    new ClassPathScanningCandidateComponentProvider(true);

            scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

            for (BeanDefinition bd : scanner.findCandidateComponents(controllerPackage)){
                controllerClasses.add(Class.forName(bd.getBeanClassName()));
            }

            return controllerClasses.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(controllerClasses);

        }else{

            return Collections.emptyList();
        }
    }

    static Optional<String> getRequestMappingOptional(Class<?> controllerClass) {

        final RequestMapping requestAnnotation = controllerClass.getAnnotation(RequestMapping.class);

        if (requestAnnotation!=null && requestAnnotation.value().length!=0) {

            final String startPath = requestAnnotation.value()[0];

            return Optional.of(startPath);

        }else{

            return Optional.empty();
        }
    }
}
