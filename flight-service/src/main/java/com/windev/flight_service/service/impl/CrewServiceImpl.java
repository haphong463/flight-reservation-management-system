package com.windev.flight_service.service.impl;

import com.windev.flight_service.dto.CrewDTO;
import com.windev.flight_service.entity.Crew;
import com.windev.flight_service.entity.Flight;
import com.windev.flight_service.exception.CrewNotFoundException;
import com.windev.flight_service.exception.FlightNotFoundException;
import com.windev.flight_service.mapper.CrewMapper;
import com.windev.flight_service.payload.request.crew.CrewRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import com.windev.flight_service.repository.CrewRepository;
import com.windev.flight_service.repository.FlightRepository;
import com.windev.flight_service.service.CrewService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrewServiceImpl implements CrewService {

    private final CrewRepository crewRepository;

    private final CrewMapper crewMapper;

    private final FlightRepository flightRepository;

    @Override
    @Transactional
    public CrewDTO createCrew(CrewRequest request) {
        Crew crew = new Crew();

        crew.setCode(generateCode(request.getFirstName(), request.getLastName()));
        crewMapper.prepareCrewFromRequest(request, crew);

        Crew savedCrew = crewRepository.save(crew);

        log.info("createCrew() --> create crew ok");
        return crewMapper.toDTO(savedCrew);
    }

    @Override
    public CrewDTO getByCrewId(Long crewId) {
        Crew existingCrew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewNotFoundException("Crew with ID: " + crewId + " not found."));

        log.info("getByCrewId() --> get crew by id ok");
        return crewMapper.toDTO(existingCrew);
    }

    @Override
    public PaginatedResponse<CrewDTO> getAllCrewsPaginated(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Crew> crewPage =  crewRepository.findAll(pageable);

        List<CrewDTO> crews = crewPage.getContent().stream().map(crewMapper::toDTO).toList();


        log.info("getAllCrewsPaginated() -> get all paginated ok");
        return new PaginatedResponse<>(crews, crewPage.getNumber(), crewPage.getSize(), crewPage.getTotalElements(),
                crewPage.getTotalPages(), crewPage.isLast());
    }

    @Override
    public CrewDTO updateCrew(Long crewId, CrewRequest request) {
        Crew existingCrew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewNotFoundException("Crew with ID: " + crewId + " not found."));

        crewMapper.prepareCrewFromRequest(request, existingCrew);

        Crew savedCrew = crewRepository.save(existingCrew);

        log.info("updateCrew() --> update crew ok");
        return crewMapper.toDTO(savedCrew);
    }

    @Override
    public void deleteCrew(Long crewId) {
        Crew existingCrew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewNotFoundException("Crew with ID: " + crewId + " not found."));

        crewRepository.delete(existingCrew);
        log.info("deleteCrew() --> delete crew ok");
    }



    @Override
    public CrewDTO removeCrewFromFlight(String flightId, Set<Long> crewIds) {
        return null;
    }

    private String generateCode(String firstName, String lastName){
        char firstLetterOfFirstName = firstName.charAt(0);
        char firstLetterOfLastName = lastName.charAt(0);

        return String.valueOf(firstLetterOfFirstName) + String.valueOf(firstLetterOfLastName) + "-" + System.currentTimeMillis();
    }
}
