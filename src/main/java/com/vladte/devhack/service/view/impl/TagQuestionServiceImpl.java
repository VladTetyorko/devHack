package com.vladte.devhack.service.view.impl;

import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.Tag;
import com.vladte.devhack.service.domain.InterviewQuestionService;
import com.vladte.devhack.service.domain.TagService;
import com.vladte.devhack.service.view.TagQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the TagQuestionService interface.
 * This class handles operations related to questions filtered by tags.
 */
@Service
public class TagQuestionServiceImpl implements TagQuestionService {

    private final TagService tagService;
    private final InterviewQuestionService questionService;

    @Autowired
    public TagQuestionServiceImpl(TagService tagService, InterviewQuestionService questionService) {
        this.tagService = tagService;
        this.questionService = questionService;
    }

    @Override
    public Tag prepareQuestionsByTagModel(String tagSlug, Model model) {
        Tag tag = getTagBySlugOrThrow(tagSlug, "Tag not found");
        List<InterviewQuestion> questions = questionService.findQuestionsByTag(tag);
        model.addAttribute("questions", questions);
        model.addAttribute("tag", tag);
        return tag;
    }

    @Override
    public void setQuestionsByTagPageTitle(Model model, String tagName) {
        model.addAttribute("pageTitle", "Questions tagged with " + tagName);
    }

    @Override
    public Tag getTagBySlugOrThrow(String tagSlug, String errorMessage) {
        Optional<Tag> tagOpt = tagService.findTagBySlug(tagSlug);
        if (tagOpt.isPresent()) {
            return tagOpt.get();
        }
        throw new RuntimeException(errorMessage);
    }
}