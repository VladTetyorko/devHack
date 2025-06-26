package com.vladte.devhack.service.view.impl;

import com.vladte.devhack.model.InterviewStage;
import com.vladte.devhack.model.User;
import com.vladte.devhack.model.VacancyResponse;
import com.vladte.devhack.service.domain.UserService;
import com.vladte.devhack.service.domain.VacancyResponseService;
import com.vladte.devhack.service.view.VacancyResponseFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the VacancyResponseFormService interface.
 * This class handles form-related operations for vacancy responses.
 */
@Service
public class VacancyResponseFormServiceImpl implements VacancyResponseFormService {

    private final VacancyResponseService vacancyResponseService;
    private final UserService userService;

    @Autowired
    public VacancyResponseFormServiceImpl(VacancyResponseService vacancyResponseService, UserService userService) {
        this.vacancyResponseService = vacancyResponseService;
        this.userService = userService;
    }

    @Override
    public void prepareNewVacancyResponseForm(Model model) {
        model.addAttribute("vacancyResponse", new VacancyResponse());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("interviewStages", InterviewStage.values());
    }

    @Override
    public VacancyResponse prepareEditVacancyResponseForm(UUID id, Model model) {
        Optional<VacancyResponse> vacancyResponseOpt = vacancyResponseService.findById(id);
        if (vacancyResponseOpt.isPresent()) {
            VacancyResponse vacancyResponse = vacancyResponseOpt.get();
            model.addAttribute("vacancyResponse", vacancyResponse);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("interviewStages", InterviewStage.values());
            return vacancyResponse;
        }
        return null;
    }

    @Override
    public VacancyResponse saveVacancyResponse(VacancyResponse vacancyResponse, UUID userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            vacancyResponse.setUser(user);
            return vacancyResponseService.save(vacancyResponse);
        }
        return null;
    }

    @Override
    public void deleteVacancyResponse(UUID id) {
        vacancyResponseService.deleteById(id);
    }

    @Override
    public void setNewVacancyResponsePageTitle(Model model) {
        model.addAttribute("pageTitle", "New Vacancy Response");
    }

    @Override
    public void setEditVacancyResponsePageTitle(Model model) {
        model.addAttribute("pageTitle", "Edit Vacancy Response");
    }
}