package com.vladte.devhack.repository;

import com.vladte.devhack.model.VacancyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for accessing and manipulating VacancyResponse entities.
 */
@Repository
public interface VacancyResponseRepository extends JpaRepository<VacancyResponse, UUID>, JpaSpecificationExecutor<VacancyResponse> {

    // All query methods have been replaced with specifications
    // See VacancyResponseSpecification class for the available specifications

}
