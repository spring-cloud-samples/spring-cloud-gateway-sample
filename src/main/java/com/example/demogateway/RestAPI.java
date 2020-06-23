package com.example.demogateway;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestAPI {

    @RequestMapping("/hystrixfallback")
    public String hystrixfallback() {
        return "This is a fallback";
    }

    @GetMapping("/hello")
    public String hello(ServerHttpRequest request){
        request.getHeaders().forEach((key, value) -> System.out.println(key + " " + value));
        return "Hello, from spring boot application";
    }

}
