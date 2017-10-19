package com.example.demogateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.Routes;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.HttpSecurity;
import org.springframework.security.core.userdetails.MapUserDetailsRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.cloud.gateway.filter.factory.GatewayFilters.hystrix;
import static org.springframework.cloud.gateway.filter.factory.GatewayFilters.rewritePath;
import static org.springframework.cloud.gateway.handler.predicate.RoutePredicates.host;
import static org.springframework.cloud.gateway.handler.predicate.RoutePredicates.path;

@SpringBootApplication
public class DemogatewayApplication {

	@Bean
	public RouteLocator customRouteLocator(RequestRateLimiterGatewayFilterFactory rateLimiter) {
		return Routes.locator()
				.route("path_route")
					.predicate(path("/get"))
					.uri("http://httpbin.org:80")
				.route("host_route")
					.predicate(host("*.myhost.org"))
					.uri("http://httpbin.org:80")
				.route("rewrite_route")
					.predicate(host("*.rewrite.org"))
					.filter(rewritePath("/foo/(?<segment>.*)", "/${segment}"))
					.uri("http://httpbin.org:80")
				.route("hystrix_route")
					.predicate(host("*.hystrix.org"))
					.filter(hystrix("slowcmd"))
					.uri("http://httpbin.org:80")
				.route("limit_route")
					.predicate(host("*.limited.org").and(path("/anything/**")))
					.filter(rateLimiter.apply(1, 2))
					.uri("http://httpbin.org:80")
				.route("websocket_route")
					.predicate(path("/echo"))
					.uri("ws://localhost:9000")
				.build();
	}

	@Bean
	SecurityWebFilterChain springWebFilterChain(HttpSecurity http) throws Exception {
		return http.httpBasic().and()
				.authorizeExchange()
				.pathMatchers("/anything/**").authenticated()
				.anyExchange().permitAll()
				.and()
				.build();
	}

	@Bean
	public MapUserDetailsRepository userDetailsRepository() {
		UserDetails user = User.withUsername("user").password("password").roles("USER").build();
		return new MapUserDetailsRepository(user);
	}

	public static void main(String[] args) {
		SpringApplication.run(DemogatewayApplication.class, args);
	}
}
