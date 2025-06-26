package com.vladte.devhack.service.api.impl;

import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.Tag;
import com.vladte.devhack.service.domain.InterviewQuestionService;
import com.vladte.devhack.service.domain.TagService;
import com.vladte.devhack.service.domain.UserService;
import com.vladte.devhack.service.api.OpenAiService;
import com.vladte.devhack.service.api.QuestionGenerationService;
import com.vladte.devhack.service.api.QuestionParsingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of the QuestionGenerationService interface for generating interview questions using AI.
 */
@Service
public class QuestionGenerationServiceImpl implements QuestionGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionGenerationServiceImpl.class);

    private final OpenAiService openAiService;
    private final TagService tagService;
    private final UserService userService;
    private final InterviewQuestionService questionService;
    private final QuestionParsingService questionParsingService;
    private final QuestionGenerationService self;

    @Autowired
    public QuestionGenerationServiceImpl(
            OpenAiService openAiService,
            TagService tagService, UserService userService,
            InterviewQuestionService questionService,
            QuestionParsingService questionParsingService, @Lazy QuestionGenerationService self) {
        this.openAiService = openAiService;
        this.tagService = tagService;
        this.userService = userService;
        this.questionService = questionService;
        this.questionParsingService = questionParsingService;
        this.self = self;
    }

    /**
     * Generate interview questions based on a tag and save them to the database.
     *
     * @param tagName the name of the tag to generate questions for
     * @param count the number of questions to generate
     * @param difficulty the difficulty level of the questions
     * @return the list of generated questions
     */
    @Override
    @Async
    public CompletableFuture<List<InterviewQuestion>> generateAndSaveQuestions(String tagName, int count, String difficulty) {
        logger.info("Starting to generate {} {} difficulty questions for tag: {}", count, difficulty, tagName);

        // Find or create the tag
        Tag tag = findOrCreateTag(tagName);
        logger.debug("Using tag: {}", tag.getName());

        // Generate questions using OpenAI asynchronously
        logger.info("Generating questions using AI service asynchronously");
        try {
            // Get the generated text from the OpenAI service
            String generatedText = openAiService.generateQuestionsForTag(tagName, count, difficulty);
            logger.debug("Generated text length: {} characters", generatedText.length());

            // Parse the generated text into questions
            logger.info("Parsing generated text into questions");
            List<String> questionTexts = questionParsingService.parseQuestionTexts(generatedText);
            logger.info("Parsed {} questions from generated text", questionTexts.size());

            // Create and save question entities
            List<InterviewQuestion> savedQuestions = new ArrayList<>();
            logger.info("Saving questions to database");

            self.saveQuestionsToDatabase(questionTexts, difficulty, tag, savedQuestions);

            logger.info("Successfully generated and saved {} questions for tag: {}", savedQuestions.size(), tagName);
            return CompletableFuture.completedFuture(savedQuestions);
        } catch (Exception e) {
            logger.error("Error while processing generated questions", e);
            CompletableFuture<List<InterviewQuestion>> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Failed to process generated questions: " + e.getMessage(), e));
            return future;
        }
    }

    /**
     * Save the generated questions to the database.
     * This method is separated to allow for transaction management.
     *
     * @param questionTexts the list of question texts to save
     * @param difficulty the difficulty level of the questions
     * @param tag the tag to associate with the questions
     * @param savedQuestions the list to add the saved questions to
     */
    @Transactional
    @Override
    public void saveQuestionsToDatabase(List<String> questionTexts, String difficulty, Tag tag, List<InterviewQuestion> savedQuestions) {
        for (String questionText : questionTexts) {
            logger.debug("Processing question: {}", questionText.length() > 50 ? questionText.substring(0, 47) + "..." : questionText);

            InterviewQuestion question = new InterviewQuestion();
            question.setQuestionText(questionText);
            question.setDifficulty(difficulty);
            question.setUser(userService.getSystemUser());

            // Add the tag to the question
            Set<Tag> tags = new HashSet<>();
            tags.add(tag);
            question.setTags(tags);

            // Save the question
            InterviewQuestion savedQuestion = questionService.save(question);
            savedQuestions.add(savedQuestion);
            logger.debug("Saved question with ID: {}", savedQuestion.getId());
        }
    }

    /**
     * Find an existing tag by name or create a new one if it doesn't exist.
     *
     * @param tagName the name of the tag
     * @return the tag entity
     */
    private Tag findOrCreateTag(String tagName) {
        logger.debug("Finding or creating tag: {}", tagName);
        Optional<Tag> existingTag = tagService.findTagByName(tagName);
        if (existingTag.isPresent()) {
            logger.debug("Found existing tag: {}", tagName);
            return existingTag.get();
        } else {
            logger.info("Creating new tag: {}", tagName);
            Tag newTag = new Tag();
            newTag.setName(tagName);
            Tag savedTag = tagService.save(newTag);
            logger.debug("Created new tag with ID: {}", savedTag.getId());
            return savedTag;
        }
    }
}
