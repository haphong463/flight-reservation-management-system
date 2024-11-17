package com.windev.api_gateway.handler;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@Slf4j
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error(ex.getMessage());
        HttpStatus status = determineHttpStatus(ex);
        String errorMessage = determineErrorMessage(ex);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);

        byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
        );
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        log.error(ex.getMessage());
        // Customize the status code based on the exception type
         if (ex instanceof io.jsonwebtoken.JwtException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        }
        // Default to 500 Internal Server Error for other exceptions
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String determineErrorMessage(Throwable ex) {
        log.error(ex.getMessage());
        // Use a generic message or extract the exception message
        if (ex instanceof io.jsonwebtoken.JwtException) {
            return "Invalid authentication token.";
        } else if (ex instanceof IllegalArgumentException) {
            return "Invalid request parameters.";
        }
        // Default generic error message for other exceptions
        return "An unexpected error occurred.";
    }
}
