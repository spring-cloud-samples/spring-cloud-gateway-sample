package com.example.demogateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
public class DemogatewayApplication {

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("path_route")
					.path("/get")
					.uri("http://httpbin.org:80")
				.route("host_route")
					.host("*.myhost.org")
					.uri("http://httpbin.org:80")
				.route("rewrite_route")
					.host("*.rewrite.org")
					.rewritePath("/foo/(?<segment>.*)", "/${segment}")
					.uri("http://httpbin.org:80")
				.route("hystrix_route")
					.host("*.hystrix.org")
					.hystrix("slowcmd")
					.uri("http://httpbin.org:80")
				.route("limit_route")
					.host("*.limited.org").and().path("/anything/**")
					.requestRateLimiter(RedisRateLimiter.args(1, 2))
					.uri("http://httpbin.org:80")
				.route("websocket_route")
					.path("/echo")
					.uri("ws://localhost:9000")
				.build();
	}

	@Bean
	SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
		return http.httpBasic().and()
				.authorizeExchange()
				.pathMatchers("/anything/**").authenticated()
				.anyExchange().permitAll()
				.and()
				.build();
	}

	@Bean
	public MapReactiveUserDetailsService mapReactiveUserDetailsService() {
		UserDetails user = User.withUsername("user").password("{noop}password").roles("USER").build();
		return new MapReactiveUserDetailsService(user);
	}

	public static void main(String[] args) {
		SpringApplication.run(DemogatewayApplication.class, args);
	}
}
