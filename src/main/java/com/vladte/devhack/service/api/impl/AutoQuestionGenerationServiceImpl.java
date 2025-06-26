package com.vladte.devhack.service.api.impl;

import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.Tag;
import com.vladte.devhack.service.domain.InterviewQuestionService;
import com.vladte.devhack.service.domain.TagService;
import com.vladte.devhack.service.domain.UserService;
import com.vladte.devhack.service.api.AutoQuestionGenerationService;
import com.vladte.devhack.service.api.OpenAiService;
import com.vladte.devhack.service.api.QuestionParsingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Implementation of the AutoQuestionGenerationService interface for automatically
 * generating easy interview questions for tags using OpenAI.
 */
@Service
public class AutoQuestionGenerationServiceImpl implements AutoQuestionGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(AutoQuestionGenerationServiceImpl.class);
    private static final String DIFFICULTY = "easy";
    private static final int QUESTION_COUNT = 3;

    private final OpenAiService openAiService;
    private final TagService tagService;
    private final UserService userService;
    private final InterviewQuestionService questionService;
    private final QuestionParsingService questionParsingService;

    @Autowired
    public AutoQuestionGenerationServiceImpl(
            OpenAiService openAiService,
            TagService tagService,
            UserService userService,
            InterviewQuestionService questionService,
            QuestionParsingService questionParsingService) {
        this.openAiService = openAiService;
        this.tagService = tagService;
        this.userService = userService;
        this.questionService = questionService;
        this.questionParsingService = questionParsingService;
    }

    /**
     * Generate 3 easy interview questions for a specific tag using OpenAI.
     * This method processes one tag per operation and makes one request to OpenAI per tag.
     *
     * @param tag the tag to generate questions for
     * @return a CompletableFuture containing the list of generated questions
     */
    @Override
    @Async
    public CompletableFuture<List<InterviewQuestion>> generateEasyQuestionsForTag(Tag tag) {
        logger.info("Starting to generate {} {} difficulty questions for tag: {}", 
                QUESTION_COUNT, DIFFICULTY, tag.getName());

        try {
            // Generate questions using OpenAI asynchronously
            logger.info("Generating questions using AI service asynchronously for tag: {}", tag.getName());

            // Make one request to OpenAI for this tag
            CompletableFuture<String> generatedTextFuture = openAiService.generateQuestionsForTagAsync(
                    tag.getName(), QUESTION_COUNT, DIFFICULTY);

            // Process the result when it's available
            return generatedTextFuture.thenApply(generatedText -> {
                logger.debug("Generated text length: {} characters", generatedText.length());

                // Parse the generated text into questions
                logger.info("Parsing generated text into questions");
                List<String> questionTexts = questionParsingService.parseQuestionTexts(generatedText);
                logger.info("Parsed {} questions from generated text", questionTexts.size());

                // Create and save question entities
                List<InterviewQuestion> savedQuestions = new ArrayList<>();
                logger.info("Saving questions to database");

                saveQuestionsToDatabase(questionTexts, tag, savedQuestions);

                logger.info("Successfully generated and saved {} questions for tag: {}", 
                        savedQuestions.size(), tag.getName());
                return savedQuestions;
            }).exceptionally(ex -> {
                logger.error("Error while processing generated questions for tag {}: {}", 
                        tag.getName(), ex.getMessage(), ex);
                throw new RuntimeException("Failed to process generated questions: " + ex.getMessage(), ex);
            });
        } catch (Exception e) {
            logger.error("Error while generating questions for tag {}: {}", 
                    tag.getName(), e.getMessage(), e);
            CompletableFuture<List<InterviewQuestion>> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException(
                    "Failed to generate questions: " + e.getMessage(), e));
            return future;
        }
    }

    /**
     * Generate 3 easy interview questions for a specific tag name using OpenAI.
     * This method processes one tag per operation and makes one request to OpenAI per tag.
     *
     * @param tagName the name of the tag to generate questions for
     * @return a CompletableFuture containing the list of generated questions
     */
    @Override
    @Async
    public CompletableFuture<List<InterviewQuestion>> generateEasyQuestionsForTagName(String tagName) {
        logger.info("Starting to generate questions for tag name: {}", tagName);

        // Find or create the tag
        Tag tag = findOrCreateTag(tagName);
        logger.debug("Using tag: {}", tag.getName());

        // Generate questions for the tag
        return generateEasyQuestionsForTag(tag);
    }

    /**
     * Save the generated questions to the database.
     *
     * @param questionTexts the list of question texts to save
     * @param tag the tag to associate with the questions
     * @param savedQuestions the list to add the saved questions to
     */
    @Transactional
    protected void saveQuestionsToDatabase(List<String> questionTexts, Tag tag, List<InterviewQuestion> savedQuestions) {
        for (String questionText : questionTexts) {
            logger.debug("Processing question: {}", 
                    questionText.length() > 50 ? questionText.substring(0, 47) + "..." : questionText);

            InterviewQuestion question = new InterviewQuestion();
            question.setQuestionText(questionText);
            question.setDifficulty(DIFFICULTY);
            question.setSource("OpenAI Auto-Generated");
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

    /**
     * Generate 3 easy interview questions for multiple tags using OpenAI.
     * This method processes each tag separately and makes one request to OpenAI per tag.
     *
     * @param tagIds the IDs of the tags to generate questions for
     * @return a CompletableFuture containing the list of all generated questions
     */
    @Override
    @Async
    public CompletableFuture<List<InterviewQuestion>> generateEasyQuestionsForMultipleTags(List<UUID> tagIds) {
        logger.info("Starting to generate questions for multiple tags: {}", tagIds);

        if (tagIds == null || tagIds.isEmpty()) {
            logger.warn("No tags provided for question generation");
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        // Get all tags by their IDs
        List<Tag> tags = tagIds.stream()
                .map(tagService::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        logger.info("Found {} tags out of {} requested", tags.size(), tagIds.size());

        if (tags.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        // Create a list to hold all futures
        List<CompletableFuture<List<InterviewQuestion>>> futures = new ArrayList<>();

        // Generate questions for each tag asynchronously
        for (Tag tag : tags) {
            futures.add(generateEasyQuestionsForTag(tag));
        }

        // Combine all futures into one
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .collect(Collectors.toList()));
    }
}
