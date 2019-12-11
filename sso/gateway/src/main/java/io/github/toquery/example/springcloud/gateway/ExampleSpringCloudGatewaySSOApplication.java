package io.github.toquery.example.springcloud.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.security.oauth2.gateway.TokenRelayGatewayFilterFactory;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class ExampleSpringCloudGatewaySSOApplication {

    @Value("${app.resource.uri:http://resource:9000}")
    private String uri;

    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringCloudGatewaySSOApplication.class, args);
    }

    @Autowired
    private TokenRelayGatewayFilterFactory filterFactory;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("resource", r -> r.path("/resource")
                        .filters(f -> f.filters(filterFactory.apply())
                                .removeRequestHeader("Cookie")) // Prevents cookie being sent downstream
                        .uri(uri)) // Taking advantage of docker naming
                .build();
        //@formatter:on
    }

}
