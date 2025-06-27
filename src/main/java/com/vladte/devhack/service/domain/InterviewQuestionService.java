package com.vladte.devhack.service.domain;

import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for InterviewQuestion entity operations.
 */
public interface InterviewQuestionService extends BaseService<InterviewQuestion, UUID> {
    /**
     * Find questions by difficulty.
     *
     * @param difficulty the difficulty level
     * @return a list of questions with the specified difficulty
     */
    List<InterviewQuestion> findQuestionsByDifficulty(String difficulty);

    /**
     * Find questions by tag.
     *
     * @param tag the tag
     * @return a list of questions with the specified tag
     */
    List<InterviewQuestion> findQuestionsByTag(Tag tag);

    /**
     * Find questions by difficulty and tag.
     *
     * @param difficulty the difficulty level
     * @param tag        the tag
     * @return a list of questions with the specified difficulty and tag
     */
    List<InterviewQuestion> findQuestionsByDifficultyAndTag(String difficulty, Tag tag);

    /**
     * Find questions by difficulty with pagination.
     *
     * @param difficulty the difficulty level
     * @param pageable   pagination information
     * @return a page of questions with the specified difficulty
     */
    Page<InterviewQuestion> findQuestionsByDifficulty(String difficulty, Pageable pageable);

    /**
     * Find questions by tag with pagination.
     *
     * @param tag      the tag
     * @param pageable pagination information
     * @return a page of questions with the specified tag
     */
    Page<InterviewQuestion> findQuestionsByTag(Tag tag, Pageable pageable);

    /**
     * Find questions by difficulty and tag with pagination.
     *
     * @param difficulty the difficulty level
     * @param tag        the tag
     * @param pageable   pagination information
     * @return a page of questions with the specified difficulty and tag
     */
    Page<InterviewQuestion> findQuestionsByDifficultyAndTag(String difficulty, Tag tag, Pageable pageable);

    /**
     * Search questions with filtering and pagination.
     *
     * @param query      the search query
     * @param difficulty the difficulty level
     * @param tagId      the tag ID
     * @param pageable   pagination information
     * @return a page of questions matching the search criteria
     */
    Page<InterviewQuestion> searchQuestions(String query, String difficulty, UUID tagId, Pageable pageable);
}
