package com.vladte.devhack.service.domain.impl;

import com.vladte.devhack.model.InterviewStage;
import com.vladte.devhack.model.User;
import com.vladte.devhack.model.VacancyResponse;
import com.vladte.devhack.repository.VacancyResponseRepository;
import com.vladte.devhack.repository.specification.VacancyResponseSpecification;
import com.vladte.devhack.service.domain.VacancyResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of the VacancyResponseService interface.
 */
@Service
public class VacancyResponseServiceImpl extends UserOwnedServiceImpl<VacancyResponse, UUID, VacancyResponseRepository> implements VacancyResponseService {

    /**
     * Constructor with repository injection.
     *
     * @param repository the vacancy response repository
     */
    @Autowired
    public VacancyResponseServiceImpl(VacancyResponseRepository repository) {
        super(repository);
    }

    @Override
    public List<VacancyResponse> getVacancyResponsesByUser(User user) {
        // Use specification instead of direct query
        return repository.findAll(VacancyResponseSpecification.byUser(user));
    }

    @Override
    public Page<VacancyResponse> getVacancyResponsesByUser(User user, Pageable pageable) {
        // Use specification instead of direct query
        return repository.findAll(VacancyResponseSpecification.byUser(user), pageable);
    }

    @Override
    public List<VacancyResponse> getVacancyResponsesByUserId(UUID userId) {
        // Use specification instead of direct query
        return repository.findAll(VacancyResponseSpecification.byUserId(userId));
    }

    @Override
    public Page<VacancyResponse> getVacancyResponsesByUserId(UUID userId, Pageable pageable) {
        // Use specification instead of direct query
        return repository.findAll(VacancyResponseSpecification.byUserId(userId), pageable);
    }

    @Override
    public List<VacancyResponse> getVacancyResponsesByCompanyName(String companyName) {
        // Use specification instead of direct query
        return repository.findAll(VacancyResponseSpecification.byCompanyNameContainingIgnoreCase(companyName));
    }

    @Override
    public Page<VacancyResponse> getVacancyResponsesByCompanyName(String companyName, Pageable pageable) {
        // Use specification instead of direct query
        return repository.findAll(VacancyResponseSpecification.byCompanyNameContainingIgnoreCase(companyName), pageable);
    }

    @Override
    public List<VacancyResponse> getVacancyResponsesByPosition(String position) {
        // Use specification instead of direct query
        return repository.findAll(VacancyResponseSpecification.byPositionContainingIgnoreCase(position));
    }

    @Override
    public Page<VacancyResponse> getVacancyResponsesByPosition(String position, Pageable pageable) {
        // Use specification instead of direct query
        return repository.findAll(VacancyResponseSpecification.byPositionContainingIgnoreCase(position), pageable);
    }

    @Override
    public Page<VacancyResponse> searchVacancyResponses(String query, InterviewStage stage, Pageable pageable) {
        return repository.findAll(
                VacancyResponseSpecification.searchVacancyResponses(query, stage),
                pageable
        );
    }

    @Override
    protected User getEntityUser(VacancyResponse entity) {
        return entity.getUser();
    }
}
