package com.windev.flight_service.mapper;

import com.windev.flight_service.dto.FlightDTO;
import com.windev.flight_service.dto.FlightDetailDTO;
import com.windev.flight_service.entity.Flight;
import com.windev.flight_service.payload.request.CreateFlightRequest;
import com.windev.flight_service.payload.request.UpdateFlightRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FlightMapper {
    FlightDTO toDTO(Flight flight);

    FlightDetailDTO toDetailDTO(Flight flight);

    FlightDetailDTO fromFlightDTOtoDetail(FlightDTO flightDTO);

    void createFlightFromRequest(CreateFlightRequest request, @MappingTarget Flight flight);

    void updateFlightFromRequest(UpdateFlightRequest request, @MappingTarget Flight flight);

}
