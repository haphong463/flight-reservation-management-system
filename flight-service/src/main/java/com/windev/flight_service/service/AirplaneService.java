package com.windev.flight_service.service;

import com.windev.flight_service.dto.AirplaneDTO;
import com.windev.flight_service.payload.request.airplane.AirplaneRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;



public interface AirplaneService {
    AirplaneDTO createAirplane(AirplaneRequest request);
    void deleteAirplane(String id);
    PaginatedResponse<AirplaneDTO> getAllAirplanes(int pageNumber, int pageSize);
}
