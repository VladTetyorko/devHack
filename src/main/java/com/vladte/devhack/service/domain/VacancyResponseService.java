package com.vladte.devhack.service.domain;

import com.vladte.devhack.model.InterviewStage;
import com.vladte.devhack.model.User;
import com.vladte.devhack.model.VacancyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing VacancyResponse entities.
 */
public interface VacancyResponseService extends BaseService<VacancyResponse, UUID> {

    /**
     * Get all vacancy responses for a specific user.
     *
     * @param user the user to get vacancy responses for
     * @return a list of vacancy responses
     */
    List<VacancyResponse> getVacancyResponsesByUser(User user);

    /**
     * Get all vacancy responses for a specific user with pagination.
     *
     * @param user     the user to get vacancy responses for
     * @param pageable the pagination information
     * @return a page of vacancy responses
     */
    Page<VacancyResponse> getVacancyResponsesByUser(User user, Pageable pageable);

    /**
     * Get all vacancy responses for a specific user ID.
     *
     * @param userId the ID of the user to get vacancy responses for
     * @return a list of vacancy responses
     */
    List<VacancyResponse> getVacancyResponsesByUserId(UUID userId);

    /**
     * Get all vacancy responses for a specific user ID with pagination.
     *
     * @param userId   the ID of the user to get vacancy responses for
     * @param pageable the pagination information
     * @return a page of vacancy responses
     */
    Page<VacancyResponse> getVacancyResponsesByUserId(UUID userId, Pageable pageable);

    /**
     * Get all vacancy responses for a specific company name.
     *
     * @param companyName the company name to get vacancy responses for
     * @return a list of vacancy responses
     */
    List<VacancyResponse> getVacancyResponsesByCompanyName(String companyName);

    /**
     * Get all vacancy responses for a specific company name with pagination.
     *
     * @param companyName the company name to get vacancy responses for
     * @param pageable    the pagination information
     * @return a page of vacancy responses
     */
    Page<VacancyResponse> getVacancyResponsesByCompanyName(String companyName, Pageable pageable);

    /**
     * Get all vacancy responses for a specific position.
     *
     * @param position the position to get vacancy responses for
     * @return a list of vacancy responses
     */
    List<VacancyResponse> getVacancyResponsesByPosition(String position);

    /**
     * Get all vacancy responses for a specific position with pagination.
     *
     * @param position the position to get vacancy responses for
     * @param pageable the pagination information
     * @return a page of vacancy responses
     */
    Page<VacancyResponse> getVacancyResponsesByPosition(String position, Pageable pageable);

    /**
     * Search for vacancy responses by query and interview stage with pagination.
     *
     * @param query    the search query for company name, position, or technologies
     * @param stage    the interview stage to filter by
     * @param pageable the pagination information
     * @return a page of vacancy responses
     */
    Page<VacancyResponse> searchVacancyResponses(String query, InterviewStage stage, Pageable pageable);
}
