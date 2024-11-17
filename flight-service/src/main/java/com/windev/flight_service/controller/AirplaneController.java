package com.windev.flight_service.controller;

import com.windev.flight_service.dto.AirplaneDTO;
import com.windev.flight_service.payload.request.airplane.AirplaneRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import com.windev.flight_service.service.AirplaneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/airplanes")
@RestController
@RequiredArgsConstructor
public class AirplaneController {
    private final AirplaneService airplaneService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<AirplaneDTO>> getAllAirplanes(@RequestParam(defaultValue = "0") int pageNumber,
                                                                          @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(airplaneService.getAllAirplanes(pageNumber, pageSize));
    }

    @PostMapping
    public ResponseEntity<AirplaneDTO> createAirplane(@RequestBody AirplaneRequest request) {
        AirplaneDTO result = airplaneService.createAirplane(request);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAirplane(@PathVariable String id){
        airplaneService.deleteAirplane(id);
        return ResponseEntity.noContent().build();
    }
}
