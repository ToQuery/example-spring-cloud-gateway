# example-spring-cloud-gateway


添加接口的权重和负载均衡

```yaml
spring:
  application:
    name: cloud-gateway
  cloud:
    gateway:
      routes:
      - id: service1_prod
        uri: http://localhost:8081
        predicates:
        - Path=/test
        - Weight=service1, 50
      - id: service1_canary
        uri: http://localhost:8082
        predicates:
        - Path=/test
        - Weight=service1, 50
```
