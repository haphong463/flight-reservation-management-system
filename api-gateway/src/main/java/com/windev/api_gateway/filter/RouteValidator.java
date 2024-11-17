package com.windev.api_gateway.filter;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {
    public static final List<String> openAipEndpoints = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
                "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            req -> openAipEndpoints.stream().noneMatch(uri -> req.getURI().getPath().contains(uri));
}
