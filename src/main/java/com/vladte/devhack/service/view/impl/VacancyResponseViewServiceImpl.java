package com.vladte.devhack.service.view.impl;

import com.vladte.devhack.dto.VacancyResponseDTO;
import com.vladte.devhack.mapper.VacancyResponseMapper;
import com.vladte.devhack.model.InterviewStage;
import com.vladte.devhack.model.User;
import com.vladte.devhack.model.VacancyResponse;
import com.vladte.devhack.service.domain.UserService;
import com.vladte.devhack.service.domain.VacancyResponseService;
import com.vladte.devhack.service.view.VacancyResponseViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the VacancyResponseViewService interface.
 * This class prepares the model for the vacancy response views.
 */
@Service
public class VacancyResponseViewServiceImpl implements VacancyResponseViewService {

    private final VacancyResponseService vacancyResponseService;
    private final UserService userService;
    private final VacancyResponseMapper vacancyResponseMapper;

    @Autowired
    public VacancyResponseViewServiceImpl(VacancyResponseService vacancyResponseService,
                                          UserService userService,
                                          VacancyResponseMapper vacancyResponseMapper) {
        this.vacancyResponseService = vacancyResponseService;
        this.userService = userService;
        this.vacancyResponseMapper = vacancyResponseMapper;
    }

    @Override
    public Page<VacancyResponseDTO> prepareCurrentUserVacancyResponsesModel(Model model) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        // Find the user by email
        User currentUser = userService.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        // Get vacancy responses for the current user
        List<VacancyResponse> vacancyResponses = vacancyResponseService.getVacancyResponsesByUser(currentUser);

        // Convert entities to DTOs
        List<VacancyResponseDTO> vacancyResponseDTOs = vacancyResponses.stream()
                .map(vacancyResponseMapper::toDTO)
                .collect(Collectors.toList());

        // Add DTOs to model
        model.addAttribute("vacancyResponses", vacancyResponseDTOs);
        model.addAttribute("user", currentUser);

        // Create a Page object with the DTOs
        return new PageImpl<>(vacancyResponseDTOs);
    }

    @Override
    public void setCurrentUserVacancyResponsesPageTitle(Model model) {
        model.addAttribute("pageTitle", "My Vacancy Responses");
    }

    @Override
    public void prepareSearchResultsModel(String query, String stage, int page, int size, Model model) {
        // Convert stage string to enum if provided
        InterviewStage interviewStage = null;
        if (stage != null && !stage.isEmpty()) {
            try {
                interviewStage = InterviewStage.valueOf(stage);
            } catch (IllegalArgumentException e) {
                // Invalid stage parameter, ignore it
            }
        }

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Search with pagination
        Page<VacancyResponse> vacancyResponsePage = vacancyResponseService.searchVacancyResponses(query, interviewStage, pageable);

        // Convert entities to DTOs
        List<VacancyResponseDTO> vacancyResponseDTOs = vacancyResponsePage.getContent().stream()
                .map(vacancyResponseMapper::toDTO)
                .collect(Collectors.toList());

        // Add pagination data to model
        model.addAttribute("vacancyResponses", vacancyResponseDTOs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vacancyResponsePage.getTotalPages());
        model.addAttribute("totalItems", vacancyResponsePage.getTotalElements());
        model.addAttribute("size", size);

        // Add filter parameters to model for maintaining state in the view
        model.addAttribute("query", query);
        model.addAttribute("stage", stage);

    }

    @Override
    public void setSearchResultsPageTitle(Model model) {
        model.addAttribute("pageTitle", "Vacancy Responses");
    }

    @Override
    public User prepareUserVacancyResponsesModel(UUID userId, int page, int size, Model model) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Create pageable object
            Pageable pageable = PageRequest.of(page, size);

            // Get user's vacancies with pagination
            Page<VacancyResponse> vacancyResponsePage = vacancyResponseService.getVacancyResponsesByUser(user, pageable);

            // Add pagination data to model
            model.addAttribute("vacancyResponses", vacancyResponsePage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", vacancyResponsePage.getTotalPages());
            model.addAttribute("totalItems", vacancyResponsePage.getTotalElements());
            model.addAttribute("size", size);

            model.addAttribute("user", user);
            return user;
        }
        return null;
    }

    @Override
    public void setUserVacancyResponsesPageTitle(Model model, User user) {
        model.addAttribute("pageTitle", "Vacancy Responses for " + user.getName());
    }
}
