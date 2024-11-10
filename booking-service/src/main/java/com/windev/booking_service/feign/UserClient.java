package com.windev.booking_service.feign;

import com.windev.booking_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {
    @GetMapping("/api/v1/auth/me")
    ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader);
}
