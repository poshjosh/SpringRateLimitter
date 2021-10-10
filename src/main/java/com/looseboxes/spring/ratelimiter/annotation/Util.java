package com.looseboxes.spring.ratelimiter.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class Util {

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
