package com.vladte.devhack.service.api;

import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.Tag;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for generating interview questions using AI.
 */
public interface QuestionGenerationService {

    /**
     * Generate interview questions based on a tag and save them to the database asynchronously.
     *
     * @param tagName    the name of the tag to generate questions for
     * @param count      the number of questions to generate
     * @param difficulty the difficulty level of the questions
     * @return a CompletableFuture containing the list of generated questions
     */
    CompletableFuture<List<InterviewQuestion>> generateAndSaveQuestions(String tagName, int count, String difficulty);

    @Transactional
    void saveQuestionsToDatabase(List<String> questionTexts, String difficulty, Tag tag, List<InterviewQuestion> savedQuestions);
}
