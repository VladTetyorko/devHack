package com.vladte.devhack.repository;

import com.vladte.devhack.model.VacancyResponse;
import com.vladte.devhack.model.User;
import com.vladte.devhack.model.InterviewStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for accessing and manipulating VacancyResponse entities.
 */
@Repository
public interface VacancyResponseRepository extends JpaRepository<VacancyResponse, UUID>, JpaSpecificationExecutor<VacancyResponse> {

    // All query methods have been replaced with specifications
    // See VacancyResponseSpecification class for the available specifications

}
