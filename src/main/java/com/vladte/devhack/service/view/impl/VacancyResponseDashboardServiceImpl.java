package com.vladte.devhack.service.view.impl;

import com.vladte.devhack.model.InterviewStage;
import com.vladte.devhack.model.VacancyResponse;
import com.vladte.devhack.service.domain.VacancyResponseService;
import com.vladte.devhack.service.view.VacancyResponseDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the VacancyResponseDashboardService interface.
 * This class prepares the model for the vacancy response dashboard view.
 */
@Service
public class VacancyResponseDashboardServiceImpl implements VacancyResponseDashboardService {

    private final VacancyResponseService vacancyResponseService;

    @Autowired
    public VacancyResponseDashboardServiceImpl(VacancyResponseService vacancyResponseService) {
        this.vacancyResponseService = vacancyResponseService;
    }

    @Override
    public void prepareDashboardModel(Model model) {
        List<VacancyResponse> vacancyResponses = vacancyResponseService.findAll();

        // Add vacancy count to model
        model.addAttribute("vacancyCount", vacancyResponses.size());
        model.addAttribute("vacancyResponses", vacancyResponses);

        // Count vacancies by interview stage
        Map<InterviewStage, Long> stageCountMap = vacancyResponses.stream()
            .collect(Collectors.groupingBy(VacancyResponse::getInterviewStage, Collectors.counting()));

        // Add counts for each stage
        long appliedCount = stageCountMap.getOrDefault(InterviewStage.APPLIED, 0L);
        long phoneInterviewCount = stageCountMap.getOrDefault(InterviewStage.SCREENING, 0L);
        long technicalInterviewCount = stageCountMap.getOrDefault(InterviewStage.TECHNICAL_INTERVIEW, 0L);
        long finalInterviewCount = stageCountMap.getOrDefault(InterviewStage.STAKEHOLDER_INTERVIEW, 0L);
        long offerCount = stageCountMap.getOrDefault(InterviewStage.OFFER, 0L);
        long rejectedCount = stageCountMap.getOrDefault(InterviewStage.REJECTED, 0L);

        model.addAttribute("appliedCount", appliedCount);
        model.addAttribute("phoneInterviewCount", phoneInterviewCount);
        model.addAttribute("technicalInterviewCount", technicalInterviewCount);
        model.addAttribute("finalInterviewCount", finalInterviewCount);
        model.addAttribute("offerCount", offerCount);
        model.addAttribute("rejectedCount", rejectedCount);

        // Calculate percentages
        int totalVacancies = vacancyResponses.size();
        int appliedPercentage = totalVacancies > 0 ? (int)(((double)appliedCount / totalVacancies) * 100) : 0;
        int phoneInterviewPercentage = totalVacancies > 0 ? (int)(((double)phoneInterviewCount / totalVacancies) * 100) : 0;
        int technicalInterviewPercentage = totalVacancies > 0 ? (int)(((double)technicalInterviewCount / totalVacancies) * 100) : 0;
        int finalInterviewPercentage = totalVacancies > 0 ? (int)(((double)finalInterviewCount / totalVacancies) * 100) : 0;
        int offerPercentage = totalVacancies > 0 ? (int)(((double)offerCount / totalVacancies) * 100) : 0;
        int rejectedPercentage = totalVacancies > 0 ? (int)(((double)rejectedCount / totalVacancies) * 100) : 0;

        model.addAttribute("appliedPercentage", appliedPercentage);
        model.addAttribute("phoneInterviewPercentage", phoneInterviewPercentage);
        model.addAttribute("technicalInterviewPercentage", technicalInterviewPercentage);
        model.addAttribute("finalInterviewPercentage", finalInterviewPercentage);
        model.addAttribute("offerPercentage", offerPercentage);
        model.addAttribute("rejectedPercentage", rejectedPercentage);
    }

    @Override
    public Pageable prepareDashboardModel(int page, int size, Model model) {
        // Create pageable object for the vacancy responses
        Pageable pageable = PageRequest.of(page, size);

        // Get all vacancy responses with pagination for the "Top Companies" section
        Page<VacancyResponse> vacancyResponsePage = vacancyResponseService.findAll(pageable);

        // Add pagination data to model
        model.addAttribute("vacancyResponses", vacancyResponsePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vacancyResponsePage.getTotalPages());
        model.addAttribute("totalItems", vacancyResponsePage.getTotalElements());
        model.addAttribute("size", size);

        // Get all vacancy responses for statistics (no pagination needed for statistics)
        List<VacancyResponse> allVacancyResponses = vacancyResponseService.findAll();

        // Add vacancy count to model
        model.addAttribute("vacancyCount", allVacancyResponses.size());

        // Count vacancies by interview stage
        Map<InterviewStage, Long> stageCountMap = allVacancyResponses.stream()
            .collect(Collectors.groupingBy(VacancyResponse::getInterviewStage, Collectors.counting()));

        // Add counts for each stage
        long appliedCount = stageCountMap.getOrDefault(InterviewStage.APPLIED, 0L);
        long phoneInterviewCount = stageCountMap.getOrDefault(InterviewStage.SCREENING, 0L);
        long technicalInterviewCount = stageCountMap.getOrDefault(InterviewStage.TECHNICAL_INTERVIEW, 0L);
        long finalInterviewCount = stageCountMap.getOrDefault(InterviewStage.STAKEHOLDER_INTERVIEW, 0L);
        long offerCount = stageCountMap.getOrDefault(InterviewStage.OFFER, 0L);
        long rejectedCount = stageCountMap.getOrDefault(InterviewStage.REJECTED, 0L);

        model.addAttribute("appliedCount", appliedCount);
        model.addAttribute("phoneInterviewCount", phoneInterviewCount);
        model.addAttribute("technicalInterviewCount", technicalInterviewCount);
        model.addAttribute("finalInterviewCount", finalInterviewCount);
        model.addAttribute("offerCount", offerCount);
        model.addAttribute("rejectedCount", rejectedCount);

        // Calculate percentages
        int totalVacancies = allVacancyResponses.size();
        int appliedPercentage = totalVacancies > 0 ? (int)(((double)appliedCount / totalVacancies) * 100) : 0;
        int phoneInterviewPercentage = totalVacancies > 0 ? (int)(((double)phoneInterviewCount / totalVacancies) * 100) : 0;
        int technicalInterviewPercentage = totalVacancies > 0 ? (int)(((double)technicalInterviewCount / totalVacancies) * 100) : 0;
        int finalInterviewPercentage = totalVacancies > 0 ? (int)(((double)finalInterviewCount / totalVacancies) * 100) : 0;
        int offerPercentage = totalVacancies > 0 ? (int)(((double)offerCount / totalVacancies) * 100) : 0;
        int rejectedPercentage = totalVacancies > 0 ? (int)(((double)rejectedCount / totalVacancies) * 100) : 0;

        model.addAttribute("appliedPercentage", appliedPercentage);
        model.addAttribute("phoneInterviewPercentage", phoneInterviewPercentage);
        model.addAttribute("technicalInterviewPercentage", technicalInterviewPercentage);
        model.addAttribute("finalInterviewPercentage", finalInterviewPercentage);
        model.addAttribute("offerPercentage", offerPercentage);
        model.addAttribute("rejectedPercentage", rejectedPercentage);

        return pageable;
    }

    @Override
    public void setDashboardPageTitle(Model model) {
        model.addAttribute("pageTitle", "Work Dashboard");
    }
}
