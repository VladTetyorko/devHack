package com.vladte.devhack.service.view.impl;

import com.vladte.devhack.model.InterviewStage;
import com.vladte.devhack.model.User;
import com.vladte.devhack.model.VacancyResponse;
import com.vladte.devhack.service.domain.UserService;
import com.vladte.devhack.service.domain.VacancyResponseService;
import com.vladte.devhack.service.view.VacancyResponseViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the VacancyResponseViewService interface.
 * This class prepares the model for the vacancy response views.
 */
@Service
public class VacancyResponseViewServiceImpl implements VacancyResponseViewService {

    private final VacancyResponseService vacancyResponseService;
    private final UserService userService;

    @Autowired
    public VacancyResponseViewServiceImpl(VacancyResponseService vacancyResponseService, UserService userService) {
        this.vacancyResponseService = vacancyResponseService;
        this.userService = userService;
    }

    @Override
    public Pageable prepareSearchResultsModel(String query, String stage, int page, int size, Model model) {
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

        // Add pagination data to model
        model.addAttribute("vacancyResponses", vacancyResponsePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vacancyResponsePage.getTotalPages());
        model.addAttribute("totalItems", vacancyResponsePage.getTotalElements());
        model.addAttribute("size", size);

        // Add filter parameters to model for maintaining state in the view
        model.addAttribute("query", query);
        model.addAttribute("stage", stage);

        return pageable;
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