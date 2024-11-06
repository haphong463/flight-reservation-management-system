package com.windev.flight_service.mapper;

import com.windev.flight_service.dto.FlightDTO;
import com.windev.flight_service.entity.Flight;
import com.windev.flight_service.payload.request.CreateFlightRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FlightMapper {
    FlightDTO toDTO(Flight flight);
    void createFlightFromRequest(CreateFlightRequest request, @MappingTarget Flight flight);
}
