package io.github.toquery.example.springcloud.gateway.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
public class ExampleSpringCloudGatewayReactiveApplication {

    private String uri = "http://httpbin.org:80";

    public static final String HELLO_FROM_FAKE_ACTUATOR_METRICS_GATEWAY_REQUESTS = "hello from fake /actuator/metrics/gateway.requests";

    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringCloudGatewayReactiveApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 路径路由，/get/users/toquery -> /get/users/toquery (404)
                .route("path_route", r -> r.path("/get/**")
                        .uri(uri)
                )
                // 路径路由，去除前缀
                // http://localhost/get/users/toquery -> https://api.github.com/users/toquery
                // http://localhost/rewrite/get -> http://httpbin.org:80/get
                .route("path_rewrite_route", r -> r.path("/rewrite/**")
                        .filters(f -> f.rewritePath("/rewrite/(?<path>.*)", "/${path}"))
                        .uri(uri)
                )
                // host路由
                .route("host_route", r -> r.host("*.toquery.com")
                        .uri(uri)
                )
                // host路由，去除前缀
                .route("host_rewrite_route", r -> r.host("*.rewrite.org")
                        .filters(f -> f.rewritePath("/foo/(?<segment>.*)", "/${segment}"))
                        .uri(uri)
                )

//                .route("hystrix_route", r -> r.host("*.hystrix.org")
//                        .filters(f -> f.hystrix(c -> c.setName("slowcmd")))
//                        .uri(uri))

//                .route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org")
//                        .filters(f -> f.hystrix(c -> c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback")))
//                        .uri(uri))

//				.route("limit_route", r -> r
//						.host("*.limited.org").and().path("/anything/**")
//						.filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
//						.uri(uri))

                .route(r -> r.host("**.abc.org").and().path("/anything/png")
                        .filters(f ->
                                f.prefixPath("/httpbin")
                                        .addResponseHeader("X-TestHeader", "foobar"))
                        .uri(uri)
                )
                .route("read_body_pred", r -> r.host("*.readbody.org")
                        .and().readBody(String.class,
                                s -> s.trim().equalsIgnoreCase("hi"))
                        .filters(f -> f.prefixPath("/httpbin")
                                .addResponseHeader("X-TestHeader", "read_body_pred")
                        ).uri(uri)
                )
                .route("rewrite_request_obj", r -> r.host("*.rewriterequestobj.org")
                        .filters(f -> f.prefixPath("/httpbin")
                                .addResponseHeader("X-TestHeader", "rewrite_request")
                                .modifyRequestBody(String.class, Hello.class, MediaType.APPLICATION_JSON_VALUE,
                                        (exchange, s) -> {
                                            return Mono.just(new Hello(s.toUpperCase()));
                                        })
                        ).uri(uri)
                )
                .route("rewrite_request_upper", r -> r.host("*.rewriterequestupper.org")
                        .filters(f -> f.prefixPath("/httpbin")
                                .addResponseHeader("X-TestHeader", "rewrite_request_upper")
                                .modifyRequestBody(String.class, String.class,
                                        (exchange, s) -> {
                                            return Mono.just(s.toUpperCase() + s.toUpperCase());
                                        })
                        ).uri(uri)
                )
                .route("rewrite_response_upper", r -> r.host("*.rewriteresponseupper.org")
                        .filters(f -> f.prefixPath("/httpbin")
                                .addResponseHeader("X-TestHeader", "rewrite_response_upper")
                                .modifyResponseBody(String.class, String.class,
                                        (exchange, s) -> {
                                            return Mono.just(s.toUpperCase());
                                        })
                        ).uri(uri)
                )
                .route("rewrite_response_obj", r -> r.host("*.rewriteresponseobj.org")
                        .filters(f -> f.prefixPath("/httpbin")
                                .addResponseHeader("X-TestHeader", "rewrite_response_obj")
                                .modifyResponseBody(Map.class, String.class, MediaType.TEXT_PLAIN_VALUE,
                                        (exchange, map) -> {
                                            Object data = map.get("data");
                                            return Mono.just(data.toString());
                                        })
                                .setResponseHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                        ).uri(uri)
                )
                .route(r -> r.path("/image/webp")
                        .filters(f ->
                                f.prefixPath("/httpbin")
                                        .addResponseHeader("X-AnotherHeader", "baz"))
                        .uri(uri)
                )
                .route(r -> r.order(-1)
                        .host("**.throttle.org").and().path("/get")
                        .filters(f -> f.prefixPath("/httpbin")
                                .filter(new ThrottleGatewayFilter()
                                        .setCapacity(1)
                                        .setRefillTokens(1)
                                        .setRefillPeriod(10)
                                        .setRefillUnit(TimeUnit.SECONDS)))
                        .uri(uri)
                )
                .build();
        //@formatter:on
    }

    @Bean
    public RouterFunction<ServerResponse> testFunRouterFunction() {
        RouterFunction<ServerResponse> route = RouterFunctions.route(
                RequestPredicates.path("/testfun"),
                request -> ServerResponse.ok().body(BodyInserters.fromObject("hello")));
        return route;
    }

    @Bean
    public RouterFunction<ServerResponse> testWhenMetricPathIsNotMeet() {
        RouterFunction<ServerResponse> route = RouterFunctions.route(
                RequestPredicates.path("/actuator/metrics/gateway.requests"),
                request -> ServerResponse.ok().body(BodyInserters
                        .fromObject(HELLO_FROM_FAKE_ACTUATOR_METRICS_GATEWAY_REQUESTS)));
        return route;
    }

    static class Hello {

        String message;

        Hello() {
        }

        Hello(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

}
