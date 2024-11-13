package com.windev.booking_service.feign;

import com.windev.booking_service.dto.UserDTO;
import com.windev.booking_service.payload.PaginatedResponse;
import java.util.List;
import java.util.Set;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {
    @GetMapping("/api/v1/auth/me")
    ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader);

    @GetMapping("/api/v1/users/ids")
    ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam("ids") Set<String> ids);
}
