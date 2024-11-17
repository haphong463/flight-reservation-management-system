package com.windev.flight_service.mapper;

import com.windev.flight_service.dto.AirplaneDTO;
import com.windev.flight_service.entity.Airplane;
import com.windev.flight_service.payload.request.airplane.AirplaneRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AirplaneMapper {
    Airplane toEntity(AirplaneRequest request);
    AirplaneDTO toDTO(Airplane airplane);
}
