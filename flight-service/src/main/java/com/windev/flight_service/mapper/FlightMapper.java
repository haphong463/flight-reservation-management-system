package com.windev.flight_service.mapper;

import com.windev.flight_service.dto.FlightDTO;
import com.windev.flight_service.dto.FlightDetailDTO;
import com.windev.flight_service.entity.Flight;
import com.windev.flight_service.payload.request.flight.CreateFlightRequest;
import com.windev.flight_service.payload.request.flight.UpdateFlightRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FlightMapper {
    FlightDTO toDTO(Flight flight);

    @Mapping(target = "crews", source = "crews")
    @Mapping(target = "airplane", source = "airplane")
    FlightDetailDTO toDetailDTO(Flight flight);


    Flight toEntity(FlightDetailDTO flightDetailDTO);

    void createFlightFromRequest(CreateFlightRequest request, @MappingTarget Flight flight);

    void updateFlightFromRequest(UpdateFlightRequest request, @MappingTarget Flight flight);

}
