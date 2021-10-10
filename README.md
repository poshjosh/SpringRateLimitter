## Spring rate limitter

Light weight rate limiting library for spring rest controllers.

With rate limting, you can block your endpoints after some requests for specified period of time

# Usage

__1. Annotate your spring application class as shown:__

```java
@SpringBootApplication(scanBasePackageClasses = {
        com.looseboxes.spring.ratelimiter.config.RateLimiterConfiguration.class
})
@EnableConfigurationProperties({
        com.looseboxes.spring.ratelimiter.config.RateLimitProperties.class
})
@ServletComponentScan // Required for scanning of components like @WebListener
public class MySpringApplication{
    
}
```

__2. Add some required properties__

```yaml
rate-limiter:
  disabled: false
  # If using annotations, you have to specify one package where all the controllers should be scanned for
  controller-package: com.myapplicatioon.web.rest
  rate-limits:
    per-second:
      count: 90
      duration: 1
      time-unit: SECONDS
    per-minute:
      count: 300
      duration: 1
      time-unit: MINUTES
```

__3. Add an exception handler for RateLimitException.__ 

[Exception handling for rest with Spring](https://www.baeldung.com/exception-handling-for-rest-with-spring)

__4. Annotate controller or separate methods.__

```java
import com.looseboxes.spring.ratelimiter.annotation.RateLimit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@RateLimit(limit = 100, period = 1000)
@RestController
@RequestMapping("/my/resource")
public class MyResource {

    public MyResource() {
    }

    @RateLimit(limit = 25, period = 1000)
    @GetMapping("/greet/{name}")
    public ResponseEntity<String> greet(@PathVariable String name) {
        ResponseEntity.ok("Hello " + name);
    }
}
```

# Manual usage

You can define a RateLimiter manually as shown:

```java
import com.looseboxes.spring.ratelimiter.RateLimiterImpl;
import com.looseboxes.spring.ratelimiter.cache.RateCacheInMemory;
import com.looseboxes.spring.ratelimiter.config.RateLimitProperties;
import com.looseboxes.spring.ratelimiter.rates.CountWithinDuration;
import org.springframework.stereotype.Component;

@Component
public class DefaultRateManager extends RateLimiterImpl {

    public DefaultRateManager(RateLimitProperties properties) {
        super(
                new RateCacheInMemory(),
                () -> new CountWithinDuration(),
                properties.toRateList()
    }
}
```

Then you can call the RateLimiter manually.

```java
// This will throw RateLimitExceededException if any of the rates defined in
// the properties file is exceeded
rateLimiter.record(request.getRequestURI()); 
```

# Build

```sh
mvn clean install
```
