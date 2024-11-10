package com.windev.booking_service.feign;

import com.windev.booking_service.dto.FlightDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "FLIGHT-SERVICE")
public interface FlightClient {
    @GetMapping("/api/v1/flights/{id}")
    ResponseEntity<FlightDTO> getFlightById(@PathVariable String id);
}
