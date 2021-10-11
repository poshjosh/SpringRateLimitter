package com.looseboxes.spring.ratelimiter.annotation;



import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("com.looseboxes.spring.ratelimiter.annotation.RateLimit")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RateLimitProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        annotations.forEach(annotation -> {

            Set<? extends Element> annotatedElements
                    = roundEnv.getElementsAnnotatedWith(annotation);

            annotatedElements.forEach(annotatedElement ->{

                RateLimit rateLimit = annotatedElement.getAnnotation(RateLimit.class);

                final String limitError = getErrorMessageIfInvalidLimit(rateLimit.limit(), null);
                if (limitError != null){
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, limitError);
                }

                final String periodError = getErrorMessageIfInvalidPeriod(rateLimit.period(), null);
                if(periodError != null) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, periodError);
                }
            });
        });

        return false;
    }

    public static String getErrorMessageIfInvalidLimit(int limit, String resultIfNone) {
        return limit < 0 ? "Invalid limit: " + limit : resultIfNone;
    }

    public static String getErrorMessageIfInvalidPeriod(long period, String resultIfNone) {
        return period < 0 ? "Invalid period: " + period : resultIfNone;
    }
}
