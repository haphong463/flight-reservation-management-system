package com.windev.flight_service.repository;

import com.windev.flight_service.entity.Crew;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CrewRepository extends JpaRepository<Crew, Long>, JpaSpecificationExecutor<Crew> {
    @Override
    Page<Crew> findAll(Pageable pageable);

    List<Crew> findAllByIdIn(Set<Long> ids);
}
