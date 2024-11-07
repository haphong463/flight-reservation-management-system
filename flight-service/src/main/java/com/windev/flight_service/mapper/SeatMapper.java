package com.windev.flight_service.mapper;

import com.windev.flight_service.dto.SeatDTO;
import com.windev.flight_service.entity.Seat;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    SeatDTO toDTO(Seat seat);
}
