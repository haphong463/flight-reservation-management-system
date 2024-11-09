package com.windev.flight_service.service;

import com.windev.flight_service.dto.CrewDTO;
import com.windev.flight_service.dto.CrewWithFlightsDTO;
import com.windev.flight_service.payload.request.crew.CrewRequest;
import com.windev.flight_service.payload.request.crew.SearchCrewRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import java.util.Set;

public interface CrewService {
    CrewWithFlightsDTO createCrew(CrewRequest request);

    CrewWithFlightsDTO getByCrewId(Long crewId);

    PaginatedResponse<CrewDTO> getAllCrewsPaginated(int pageNumber, int pageSize);

    CrewWithFlightsDTO updateCrew(Long crewId, CrewRequest request);

    void deleteCrew(Long crewId);

    PaginatedResponse<CrewDTO> searchCrews(SearchCrewRequest request, int pageNumber, int pageSize);
}
