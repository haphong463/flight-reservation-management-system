package com.windev.flight_service.service;

import com.windev.flight_service.dto.CrewDTO;
import com.windev.flight_service.payload.request.crew.CrewRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import java.util.Set;

public interface CrewService {
    CrewDTO createCrew(CrewRequest request);

    CrewDTO getByCrewId(Long crewId);

    PaginatedResponse<CrewDTO> getAllCrewsPaginated(int pageNumber, int pageSize);

    CrewDTO updateCrew(Long crewId, CrewRequest request);

    void deleteCrew(Long crewId);

    CrewDTO removeCrewFromFlight(String flightId, Set<Long> crewIds);
}
