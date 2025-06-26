package com.vladte.devhack.service.api.impl;

import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.Tag;
import com.vladte.devhack.service.api.QuestionGenerationOrchestrationService;
import com.vladte.devhack.service.domain.TagService;
import com.vladte.devhack.service.api.AutoQuestionGenerationService;
import com.vladte.devhack.service.api.QuestionGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of the QuestionGenerationOrchestrationService interface.
 * This class orchestrates the generation of questions and related operations.
 */
@Service
public class QuestionGenerationOrchestrationServiceImpl implements QuestionGenerationOrchestrationService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionGenerationOrchestrationServiceImpl.class);

    private final TagService tagService;
    private final QuestionGenerationService questionGenerationService;
    private final AutoQuestionGenerationService autoQuestionGenerationService;

    @Autowired
    public QuestionGenerationOrchestrationServiceImpl(
            TagService tagService,
            QuestionGenerationService questionGenerationService,
            AutoQuestionGenerationService autoQuestionGenerationService) {
        this.tagService = tagService;
        this.questionGenerationService = questionGenerationService;
        this.autoQuestionGenerationService = autoQuestionGenerationService;
    }

    @Override
    public boolean validateTagName(String tagName) {
        return StringUtils.hasText(tagName);
    }

    @Override
    public CompletableFuture<List<InterviewQuestion>> startQuestionGeneration(String tagName, int count, String difficulty) {
        logger.info("Starting asynchronous generation of {} {} difficulty questions for tag: {}", 
                count, difficulty, tagName);

        // Start the asynchronous generation process without blocking
        return questionGenerationService.generateAndSaveQuestions(tagName, count, difficulty)
            .thenApply(questions -> {
                logger.info("Successfully generated {} questions for tag: {}", questions.size(), tagName);
                return questions;
            })
            .exceptionally(ex -> {
                // Log the error but don't block the user
                logger.error("Error generating questions: {}", ex.getMessage(), ex);
                return null;
            });
    }

    @Override
    public CompletableFuture<List<InterviewQuestion>> startEasyQuestionGeneration(String tagName) {
        logger.info("Starting asynchronous generation of 3 easy questions for tag: {}", tagName);

        // Start the asynchronous generation process without blocking
        return autoQuestionGenerationService.generateEasyQuestionsForTagName(tagName)
            .thenApply(questions -> {
                logger.info("Successfully auto-generated {} easy questions for tag: {}", 
                        questions.size(), tagName);
                return questions;
            })
            .exceptionally(ex -> {
                // Log the error but don't block the user
                logger.error("Error auto-generating questions: {}", ex.getMessage(), ex);
                return null;
            });
    }

    @Override
    public Optional<Tag> findTagByName(String tagName) {
        return tagService.findTagByName(tagName);
    }

    @Override
    public String buildGenerationSuccessMessage(int count, String difficulty, String tagName) {
        return String.format("Started generating %d %s difficulty questions for tag '%s'. They will appear shortly.", 
                count, difficulty, tagName);
    }

    @Override
    public String buildEasyGenerationSuccessMessage(String tagName) {
        return String.format("Started auto-generating 3 easy questions for tag '%s'. They will appear shortly.", 
                tagName);
    }

    @Override
    public Map<String, Object> buildApiResponse(boolean success, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        if (success) {
            response.put("message", message);
        } else {
            response.put("error", message);
        }
        return response;
    }

    @Override
    public CompletableFuture<List<InterviewQuestion>> startEasyQuestionGenerationForMultipleTags(List<UUID> tagIds) {
        logger.info("Starting asynchronous generation of easy questions for multiple tags: {}", tagIds);

        // Start the asynchronous generation process without blocking
        return autoQuestionGenerationService.generateEasyQuestionsForMultipleTags(tagIds)
            .thenApply(questions -> {
                logger.info("Successfully auto-generated {} easy questions for {} tags", 
                        questions.size(), tagIds.size());
                return questions;
            })
            .exceptionally(ex -> {
                // Log the error but don't block the user
                logger.error("Error auto-generating questions for multiple tags: {}", ex.getMessage(), ex);
                return null;
            });
    }

    @Override
    public String buildMultiTagEasyGenerationSuccessMessage(int tagCount) {
        return String.format("Started auto-generating easy questions for %d tags. They will appear shortly.", 
                tagCount);
    }
}
