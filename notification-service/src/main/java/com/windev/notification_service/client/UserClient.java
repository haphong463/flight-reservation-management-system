package com.windev.notification_service.client;

import com.windev.notification_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {
    @GetMapping("/api/v1/users/{id}")
    ResponseEntity<UserDTO> getUserById(@PathVariable("id") String id);
}
