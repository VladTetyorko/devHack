package com.vladte.devhack.service.domain.impl;

import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.Tag;
import com.vladte.devhack.repository.InterviewQuestionRepository;
import com.vladte.devhack.service.domain.InterviewQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of the InterviewQuestionService interface.
 */
@Service
public class InterviewQuestionServiceImpl extends BaseServiceImpl<InterviewQuestion, UUID, InterviewQuestionRepository> implements InterviewQuestionService {

    /**
     * Constructor with repository injection.
     *
     * @param repository the interview question repository
     */
    @Autowired
    public InterviewQuestionServiceImpl(InterviewQuestionRepository repository) {
        super(repository);
    }

    @Override
    public List<InterviewQuestion> findQuestionsByDifficulty(String difficulty) {
        return repository.findByDifficulty(difficulty);
    }

    @Override
    public List<InterviewQuestion> findQuestionsByTag(Tag tag) {
        return repository.findByTagsContaining(tag);
    }

    @Override
    public List<InterviewQuestion> findQuestionsByDifficultyAndTag(String difficulty, Tag tag) {
        return repository.findByDifficultyAndTagsContaining(difficulty, tag);
    }

    @Override
    public Page<InterviewQuestion> findQuestionsByDifficulty(String difficulty, Pageable pageable) {
        return repository.findByDifficulty(difficulty, pageable);
    }

    @Override
    public Page<InterviewQuestion> findQuestionsByTag(Tag tag, Pageable pageable) {
        return repository.findByTagsContaining(tag, pageable);
    }

    @Override
    public Page<InterviewQuestion> findQuestionsByDifficultyAndTag(String difficulty, Tag tag, Pageable pageable) {
        return repository.findByDifficultyAndTagsContaining(difficulty, tag, pageable);
    }

    @Override
    public Page<InterviewQuestion> searchQuestions(String query, String difficulty, UUID tagId, Pageable pageable) {
        return repository.searchQuestions(query, difficulty, tagId, pageable);
    }
}
