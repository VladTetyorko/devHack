package com.vladte.devhack.service.view.impl;

import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.service.domain.TagService;
import com.vladte.devhack.service.view.SearchService;
import com.vladte.devhack.service.view.SearchViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.UUID;

/**
 * Implementation of the SearchViewService interface.
 * This class prepares the model for the search results view.
 */
@Service
public class SearchViewServiceImpl implements SearchViewService {

    private final SearchService searchService;
    private final TagService tagService;

    @Autowired
    public SearchViewServiceImpl(SearchService searchService, TagService tagService) {
        this.searchService = searchService;
        this.tagService = tagService;
    }

    @Override
    public Pageable prepareSearchResultsModel(String query, String difficulty, UUID tagId, int page, int size, Model model) {
        // Create pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Search questions with pagination and filtering
        Page<InterviewQuestion> questionPage = searchService.searchQuestions(
                query, difficulty, tagId, pageable);

        // Add pagination data to model
        model.addAttribute("questions", questionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", questionPage.getTotalPages());
        model.addAttribute("totalItems", questionPage.getTotalElements());

        // Add filter parameters to model for maintaining state in the view
        model.addAttribute("query", query);
        model.addAttribute("difficulty", difficulty);
        model.addAttribute("tagId", tagId);

        // Add all tags for the filter dropdown
        model.addAttribute("allTags", tagService.findAll());

        return pageable;
    }

    @Override
    public void setSearchResultsPageTitle(Model model, String query, String difficulty, UUID tagId) {
        String pageTitle = searchService.buildSearchPageTitle(query, difficulty, tagId);
        model.addAttribute("pageTitle", pageTitle);
    }
}