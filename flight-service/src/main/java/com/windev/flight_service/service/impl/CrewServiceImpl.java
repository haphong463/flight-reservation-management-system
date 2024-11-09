package com.windev.flight_service.service.impl;

import com.windev.flight_service.dto.CrewDTO;
import com.windev.flight_service.dto.CrewWithFlightsDTO;
import com.windev.flight_service.entity.Crew;
import com.windev.flight_service.exception.CrewNotFoundException;
import com.windev.flight_service.mapper.CrewMapper;
import com.windev.flight_service.payload.request.crew.CrewRequest;
import com.windev.flight_service.payload.request.crew.SearchCrewRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import com.windev.flight_service.repository.CrewRepository;
import com.windev.flight_service.repository.FlightRepository;
import com.windev.flight_service.repository.specification.CrewSpecification;
import com.windev.flight_service.service.CrewService;
import com.windev.flight_service.service.cache.CrewCacheService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrewServiceImpl implements CrewService {

    private final CrewRepository crewRepository;

    private final CrewMapper crewMapper;

    private final FlightRepository flightRepository;

    private final CrewCacheService crewCacheService;

    @Override
    @Transactional
    public CrewWithFlightsDTO createCrew(CrewRequest request) {
        Crew crew = new Crew();

        crew.setCode(generateCode(request.getFirstName(), request.getLastName()));
        crewMapper.prepareCrewFromRequest(request, crew);

        Crew savedCrew = crewRepository.save(crew);

        log.info("createCrew() --> create crew ok");

        CrewWithFlightsDTO result = crewMapper.withFlightsDTO(savedCrew);

        crewCacheService.save(result);
        return result;
    }

    @Override
    public CrewWithFlightsDTO getByCrewId(Long crewId) {
        CrewWithFlightsDTO crewInCached = crewCacheService.findById(String.valueOf(crewId));

        if(crewInCached != null){
            return crewInCached;
        }

        Crew existingCrew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewNotFoundException("Crew with ID: " + crewId + " not found."));

        CrewWithFlightsDTO result = crewMapper.withFlightsDTO(existingCrew);


        crewCacheService.save(result);

        log.info("getByCrewId() --> get crew by id ok");
        return result;
    }

    @Override
    public PaginatedResponse<CrewDTO> getAllCrewsPaginated(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Crew> crewPage = crewRepository.findAll(pageable);

        List<CrewDTO> crews = crewPage.getContent().stream().map(crewMapper::toDTO).toList();


        log.info("getAllCrewsPaginated() -> get all paginated ok");
        return new PaginatedResponse<>(crews, crewPage.getNumber(), crewPage.getSize(), crewPage.getTotalElements(),
                crewPage.getTotalPages(), crewPage.isLast());
    }

    @Override
    public CrewWithFlightsDTO updateCrew(Long crewId, CrewRequest request) {
        Crew existingCrew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewNotFoundException("Crew with ID: " + crewId + " not found."));

        crewMapper.prepareCrewFromRequest(request, existingCrew);

        Crew savedCrew = crewRepository.save(existingCrew);

        CrewWithFlightsDTO result = crewMapper.withFlightsDTO(savedCrew);

        crewCacheService.update(result);

        log.info("updateCrew() --> update crew ok");
        return result;
    }

    @Override
    public void deleteCrew(Long crewId) {
        Crew existingCrew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewNotFoundException("Crew with ID: " + crewId + " not found."));

        crewRepository.delete(existingCrew);

        crewCacheService.delete(String.valueOf(crewId));
        log.info("deleteCrew() --> delete crew ok");
    }

    @Override
    public PaginatedResponse<CrewDTO> searchCrews(SearchCrewRequest request, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Specification<Crew> specification = Specification.where(null);

        if (request.getCode() != null && !request.getCode().isEmpty()) {
            specification = specification.and(CrewSpecification.hasCodeContaining(request.getCode()));
        }
        if (request.getFirstName() != null && !request.getFirstName().isEmpty()) {
            specification = specification.and(CrewSpecification.hasFirstNameContaining(request.getFirstName()));
        }
        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            specification = specification.and(CrewSpecification.hasLastNameContaining(request.getLastName()));
        }
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            specification = specification.and(CrewSpecification.hasRole(request.getRole()));
        }
        if (request.getLicenseNumber() != null && !request.getLicenseNumber().isEmpty()) {
            specification = specification.and(CrewSpecification.hasLicenseNumberContaining(request.getLicenseNumber()));
        }
        if (request.getIssueDate() != null) {
            specification = specification.and(CrewSpecification.hasIssueDateAfterOrEqual(request.getIssueDate()));
        }
        if (request.getExpirationDate() != null) {
            specification = specification.and(CrewSpecification.hasExpirationDateBeforeOrEqual(request.getExpirationDate()));
        }
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            specification = specification.and(CrewSpecification.hasStatus(request.getStatus()));
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            specification = specification.and(CrewSpecification.hasPhoneContaining(request.getPhone()));
        }

        Page<Crew> crewPage = crewRepository.findAll(specification, pageable);

        List<CrewDTO> crews = crewPage.getContent().stream().map(crewMapper::toDTO).toList();

        return new PaginatedResponse<>(crews, crewPage.getNumber(), crewPage.getSize(), crewPage.getTotalElements(),
                crewPage.getTotalPages(), crewPage.isLast());
    }




    private String generateCode(String firstName, String lastName) {
        char firstLetterOfFirstName = firstName.charAt(0);
        char firstLetterOfLastName = lastName.charAt(0);

        return String.valueOf(firstLetterOfFirstName) + String.valueOf(firstLetterOfLastName) + "-" + System.currentTimeMillis();
    }
}
