## Spring rate limitter

Small rate limitting library for rest controllers.

Idea: you can block your endpoints after some requests for specified period of time

# Usage
1. Import RateConfig to your config or SpringBootApplication

```java
@Import({RateConfig.class})
``` 

2. Add property controller.package to your applicaiton.yml or application.properties
```yaml
controller:
  package: package-name
```

3. Add an exception handler for RateLimitException. 

4. Annotate controller or separate methods. 

Number of requests and period im milliseconds can be specified. 

Default values are 1000 requests and 1ms

# Build
```yaml
mvn clean install
```

# Versions

- 0.0.1 - basic rate limitter
- 0.0.2 - added check for rate values
