package com.vladte.devhack.service.view.impl;

import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.service.domain.InterviewQuestionService;
import com.vladte.devhack.service.domain.TagService;
import com.vladte.devhack.service.view.ModelBuilder;
import com.vladte.devhack.service.view.QuestionFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the QuestionFormService interface.
 * This class handles form-related operations for questions.
 */
@Service
public class QuestionFormServiceImpl implements QuestionFormService {

    private final InterviewQuestionService questionService;
    private final TagService tagService;

    @Autowired
    public QuestionFormServiceImpl(InterviewQuestionService questionService, TagService tagService) {
        this.questionService = questionService;
        this.tagService = tagService;
    }

    @Override
    public void prepareNewQuestionForm(Model model) {
        ModelBuilder.of(model)
                .addAttribute("question", new InterviewQuestion())
                .addAttribute("tags", tagService.findAll())
                .build();
    }

    @Override
    public InterviewQuestion prepareEditQuestionForm(UUID id, Model model) {
        Optional<InterviewQuestion> questionOpt = questionService.findById(id);
        if (questionOpt.isPresent()) {
            InterviewQuestion question = questionOpt.get();
            ModelBuilder.of(model)
                    .addAttribute("question", question)
                    .addAttribute("tags", tagService.findAll())
                    .build();
            return question;
        }
        return null;
    }

    @Override
    public InterviewQuestion saveQuestion(InterviewQuestion question) {
        return questionService.save(question);
    }

    @Override
    public void deleteQuestion(UUID id) {
        questionService.deleteById(id);
    }

    @Override
    public void setNewQuestionPageTitle(Model model) {
        ModelBuilder.of(model)
                .setPageTitle("Create New Question")
                .build();
    }

    @Override
    public void setEditQuestionPageTitle(Model model) {
        ModelBuilder.of(model)
                .setPageTitle("Edit Question")
                .build();
    }

    @Override
    public void prepareGenerateQuestionsForm(Model model) {
        ModelBuilder.of(model)
                .addAttribute("tags", tagService.findAll())
                .build();
    }

    @Override
    public void setGenerateQuestionsPageTitle(Model model) {
        ModelBuilder.of(model)
                .setPageTitle("Generate Questions with AI")
                .build();
    }

    @Override
    public void prepareAutoGenerateQuestionsForm(Model model) {
        ModelBuilder.of(model)
                .addAttribute("tags", tagService.findAll())
                .build();
    }

    @Override
    public void setAutoGenerateQuestionsPageTitle(Model model) {
        ModelBuilder.of(model)
                .setPageTitle("Auto-Generate Easy Questions with AI")
                .build();
    }
}
