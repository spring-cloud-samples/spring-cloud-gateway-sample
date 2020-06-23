package com.example.demogateway;

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Routing {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        //@formatter:off
        return builder.routes()
                .route(p -> p.path("/hello").filters(f -> f.addRequestHeader("Hello", "World")).uri("http://httpbin.org:80"))
                .route("path_route", r -> r.path("/get").uri("http://httpbin.org"))
                .route("host_route", r -> r.host("*.myhost.org").uri("http://httpbin.org"))
                .route("rewrite_route", r -> r.host("*.rewrite.org").filters(f -> f.rewritePath("/foo/(?<segment>.*)","/${segment}")).uri("http://httpbin.org"))
                .route("hystrix_route", r -> r.host("*.hystrix.org").filters(f -> f.hystrix(c -> c.setName("slowcmd"))).uri("http://httpbin.org"))
                .route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org").filters(f -> f.hystrix(c -> c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback"))).uri("http://httpbin.org"))
                .route("limit_route", r -> r.host("*.limited.org").and().path("/anything/**").filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()))).uri("http://httpbin.org"))
                .route("websocket_route", r -> r.path("/echo").uri("ws://localhost:9000"))
                .build();
        //@formatter:on
    }

    @Bean
    RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 2);
    }
}
