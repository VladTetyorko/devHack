package com.vladte.devhack.service.view.impl;

import com.vladte.devhack.service.domain.DashboardService;
import com.vladte.devhack.service.view.DashboardViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the DashboardViewService interface.
 * This class prepares the model for the dashboard view.
 */
@Service
public class DashboardViewServiceImpl implements DashboardViewService {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardViewServiceImpl(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    public void prepareDashboardModel(Model model) {
        // Get counts
        model.addAttribute("questionCount", dashboardService.getQuestionCount());
        model.addAttribute("answerCount", dashboardService.getAnswerCount());
        model.addAttribute("noteCount", dashboardService.getNoteCount());
        model.addAttribute("tagCount", dashboardService.getTagCount());

        // Add progress percentages
        Map<String, Integer> progressPercentages = dashboardService.calculateProgressPercentages();
        model.addAttribute("questionProgress", progressPercentages.get("questionProgress"));
        model.addAttribute("answerProgress", progressPercentages.get("answerProgress"));
        model.addAttribute("noteProgress", progressPercentages.get("noteProgress"));
        model.addAttribute("tagProgress", progressPercentages.get("tagProgress"));

        // Add question counts by difficulty
        Map<String, Long> questionCountsByDifficulty = dashboardService.getQuestionCountsByDifficulty();
        model.addAttribute("easyQuestionCount", questionCountsByDifficulty.get("Easy"));
        model.addAttribute("mediumQuestionCount", questionCountsByDifficulty.get("Medium"));
        model.addAttribute("hardQuestionCount", questionCountsByDifficulty.get("Hard"));

        // Add answer counts by difficulty
        Map<String, Long> answerCountsByDifficulty = dashboardService.getAnswerCountsByDifficulty();
        model.addAttribute("easyAnswerCount", answerCountsByDifficulty.get("Easy"));
        model.addAttribute("mediumAnswerCount", answerCountsByDifficulty.get("Medium"));
        model.addAttribute("hardAnswerCount", answerCountsByDifficulty.get("Hard"));

        // Add answer percentages by difficulty
        Map<String, Integer> answerPercentagesByDifficulty = dashboardService.calculateAnswerPercentagesByDifficulty();
        model.addAttribute("easyAnswerPercentage", answerPercentagesByDifficulty.get("Easy"));
        model.addAttribute("mediumAnswerPercentage", answerPercentagesByDifficulty.get("Medium"));
        model.addAttribute("hardAnswerPercentage", answerPercentagesByDifficulty.get("Hard"));

        // Add tag progress data
        Map<UUID, DashboardService.TagProgress> tagProgress = dashboardService.calculateTagProgress();

        // Create maps for the view
        Map<UUID, Integer> tagQuestionCounts = new HashMap<>();
        Map<UUID, Integer> tagAnswerCounts = new HashMap<>();
        Map<UUID, Integer> tagProgressPercentages = new HashMap<>();

        for (Map.Entry<UUID, DashboardService.TagProgress> entry : tagProgress.entrySet()) {
            UUID tagId = entry.getKey();
            DashboardService.TagProgress progress = entry.getValue();

            tagQuestionCounts.put(tagId, progress.getQuestionCount());
            tagAnswerCounts.put(tagId, progress.getAnswerCount());
            tagProgressPercentages.put(tagId, progress.getProgressPercentage());
        }

        // Pass the tags list and progress data to the view
        model.addAttribute("allTags", dashboardService.getAllTags());
        model.addAttribute("tagQuestionCounts", tagQuestionCounts);
        model.addAttribute("tagAnswerCounts", tagAnswerCounts);
        model.addAttribute("tagProgressPercentages", tagProgressPercentages);
    }

    @Override
    public void setDashboardPageTitle(Model model) {
        model.addAttribute("pageTitle", "Study Dashboard");
    }
}