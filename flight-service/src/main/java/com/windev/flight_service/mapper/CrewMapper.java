package com.windev.flight_service.mapper;

import com.windev.flight_service.dto.CrewDTO;
import com.windev.flight_service.entity.Crew;
import com.windev.flight_service.payload.request.crew.CrewRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CrewMapper {

    CrewDTO toDTO(Crew crew);

    void prepareCrewFromRequest(CrewRequest request, @MappingTarget Crew crew);
}
