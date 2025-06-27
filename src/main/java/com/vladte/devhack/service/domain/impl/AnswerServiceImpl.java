package com.vladte.devhack.service.domain.impl;

import com.vladte.devhack.model.Answer;
import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.User;
import com.vladte.devhack.repository.AnswerRepository;
import com.vladte.devhack.service.api.OpenAiService;
import com.vladte.devhack.service.domain.AnswerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of the AnswerService interface.
 */
@Service
public class AnswerServiceImpl extends UserOwnedServiceImpl<Answer, UUID, AnswerRepository> implements AnswerService {

    private static final Logger logger = LoggerFactory.getLogger(AnswerServiceImpl.class);

    private final OpenAiService openAiService;
    private final AnswerService self;

    /**
     * Constructor with repository and service injection.
     *
     * @param repository    the answer repository
     * @param openAiService the OpenAI service
     */
    @Autowired
    public AnswerServiceImpl(AnswerRepository repository, OpenAiService openAiService, @Lazy AnswerService self) {
        super(repository);
        this.openAiService = openAiService;
        this.self = self;
    }

    @Override
    public Answer save(Answer answer) {
        logger.debug("Saving answer and scheduling AI check");
        Answer saved = super.save(answer);
        logger.debug("Answer saved with ID: {}, scheduling async AI check", saved.getId());
        self.checkAnswerWithAiAsync(saved.getId());
        return saved;
    }

    @Override
    public List<Answer> findAnswersByUser(User user) {
        logger.debug("Finding answers for user ID: {}", user.getId());
        List<Answer> answers = repository.findByUser(user);
        logger.debug("Found {} answers for user ID: {}", answers.size(), user.getId());
        return answers;
    }

    @Override
    public Page<Answer> findAnswersByUser(User user, Pageable pageable) {
        logger.debug("Finding answers for user ID: {} with pagination", user.getId());
        Page<Answer> answerPage = repository.findByUser(user, pageable);
        logger.debug("Found {} answers for user ID: {} (page {} of {})",
                answerPage.getNumberOfElements(), user.getId(),
                pageable.getPageNumber(), answerPage.getTotalPages());
        return answerPage;
    }

    @Override
    public List<Answer> findAnswersByQuestion(InterviewQuestion question) {
        logger.debug("Finding answers for question ID: {}", question.getId());
        List<Answer> answers = repository.findByQuestion(question);
        logger.debug("Found {} answers for question ID: {}", answers.size(), question.getId());
        return answers;
    }

    @Override
    public List<Answer> findAnswersByUserAndQuestion(User user, InterviewQuestion question) {
        logger.debug("Finding answers for user ID: {} and question ID: {}", user.getId(), question.getId());
        List<Answer> answers = repository.findByUserAndQuestion(user, question);
        logger.debug("Found {} answers for user ID: {} and question ID: {}",
                answers.size(), user.getId(), question.getId());
        return answers;
    }

    @Override
    public Answer checkAnswerWithAi(UUID answerId) {
        logger.debug("Checking answer with AI for answer ID: {}", answerId);

        // Find the answer by ID
        Answer answer = findById(answerId)
                .orElseThrow(() -> {
                    logger.error("Answer not found with ID: {}", answerId);
                    return new NoSuchElementException("Answer not found with ID: " + answerId);
                });
        logger.debug("Found answer with ID: {}", answerId);

        // Get the question text and answer text
        String questionText = answer.getQuestion().getQuestionText();
        String answerText = answer.getText();
        logger.debug("Processing question text (length: {}) and answer text (length: {})",
                questionText.length(), answerText.length());

        try {
            // Use the AI service to check the answer and get a score and feedback
            logger.debug("Calling OpenAI service to check answer");
            Map<String, Object> result = openAiService.checkAnswerWithFeedback(questionText, answerText);
            Double score = (Double) result.get("score");
            String feedback = (String) result.get("feedback");
            logger.debug("Received AI feedback: score={}, feedback length={}", score, feedback.length());

            // Update the answer with the score and feedback
            answer.setAiScore(score);
            answer.setAiFeedback(feedback);
            logger.debug("Updated answer with AI score and feedback");

            // Save and return the updated answer
            logger.debug("Saving updated answer");
            return save(answer);
        } catch (Exception e) {
            logger.error("Error while checking answer with AI: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Async
    public CompletableFuture<Answer> checkAnswerWithAiAsync(UUID answerId) {
        logger.debug("Starting async check of answer with AI for answer ID: {}", answerId);
        try {
            Answer answer = checkAnswerWithAi(answerId);
            logger.debug("Completed async check of answer with AI for answer ID: {}", answerId);
            return CompletableFuture.completedFuture(answer);
        } catch (Exception e) {
            logger.error("Error in async check of answer with AI: {}", e.getMessage(), e);
            CompletableFuture<Answer> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    protected User getEntityUser(Answer entity) {
        return entity.getUser();
    }
}
