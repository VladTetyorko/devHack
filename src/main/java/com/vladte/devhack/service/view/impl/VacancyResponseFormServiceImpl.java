package com.vladte.devhack.service.view.impl;

import com.vladte.devhack.dto.VacancyResponseDTO;
import com.vladte.devhack.mapper.VacancyResponseMapper;
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
    private final VacancyResponseMapper vacancyResponseMapper;

    @Autowired
    public VacancyResponseFormServiceImpl(VacancyResponseService vacancyResponseService,
                                          UserService userService,
                                          VacancyResponseMapper vacancyResponseMapper) {
        this.vacancyResponseService = vacancyResponseService;
        this.userService = userService;
        this.vacancyResponseMapper = vacancyResponseMapper;
    }

    @Override
    public void prepareNewVacancyResponseForm(Model model) {
        model.addAttribute("vacancyResponse", new VacancyResponseDTO());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("interviewStages", InterviewStage.values());
    }

    @Override
    public VacancyResponseDTO prepareEditVacancyResponseForm(UUID id, Model model) {
        Optional<VacancyResponse> vacancyResponseOpt = vacancyResponseService.findById(id);
        if (vacancyResponseOpt.isPresent()) {
            VacancyResponse vacancyResponse = vacancyResponseOpt.get();
            VacancyResponseDTO vacancyResponseDTO = vacancyResponseMapper.toDTO(vacancyResponse);
            model.addAttribute("vacancyResponse", vacancyResponseDTO);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("interviewStages", InterviewStage.values());
            return vacancyResponseDTO;
        }
        return null;
    }

    @Override
    public VacancyResponseDTO saveVacancyResponse(VacancyResponseDTO vacancyResponseDTO, UUID userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            VacancyResponse vacancyResponse = vacancyResponseMapper.toEntity(vacancyResponseDTO);
            vacancyResponse.setUser(user);
            VacancyResponse savedVacancyResponse = vacancyResponseService.save(vacancyResponse);
            return vacancyResponseMapper.toDTO(savedVacancyResponse);
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
