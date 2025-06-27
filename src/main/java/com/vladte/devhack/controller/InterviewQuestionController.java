package com.vladte.devhack.controller;

import com.vladte.devhack.dto.InterviewQuestionDTO;
import com.vladte.devhack.mapper.InterviewQuestionMapper;
import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.Tag;
import com.vladte.devhack.service.api.QuestionGenerationOrchestrationService;
import com.vladte.devhack.service.domain.InterviewQuestionService;
import com.vladte.devhack.service.view.DashboardViewService;
import com.vladte.devhack.service.view.QuestionFormService;
import com.vladte.devhack.service.view.SearchViewService;
import com.vladte.devhack.service.view.TagQuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller for handling requests related to interview questions.
 * This class has been refactored to follow the Single Responsibility Principle
 * by delegating dashboard and search operations to specialized services.
 */
@Controller
@RequestMapping("/questions")
public class InterviewQuestionController extends BaseCrudController<InterviewQuestion, InterviewQuestionDTO, UUID, InterviewQuestionService, InterviewQuestionMapper> {

    private static final Logger logger = LoggerFactory.getLogger(InterviewQuestionController.class);
    private final InterviewQuestionMapper mapper;
    private final QuestionGenerationOrchestrationService questionGenerationOrchestrationService;
    private final DashboardViewService dashboardViewService;
    private final QuestionFormService questionFormService;
    private final TagQuestionService tagQuestionService;
    private final SearchViewService searchViewService;

    @Autowired
    public InterviewQuestionController(
            InterviewQuestionService questionService,
            InterviewQuestionMapper interviewQuestionMapper,
            QuestionGenerationOrchestrationService questionGenerationOrchestrationService,
            DashboardViewService dashboardViewService,
            QuestionFormService questionFormService,
            TagQuestionService tagQuestionService,
            SearchViewService searchViewService) {
        super(questionService, interviewQuestionMapper);
        this.mapper = interviewQuestionMapper;
        this.questionGenerationOrchestrationService = questionGenerationOrchestrationService;
        this.dashboardViewService = dashboardViewService;
        this.questionFormService = questionFormService;
        this.tagQuestionService = tagQuestionService;
        this.searchViewService = searchViewService;
    }

    @Override
    protected String getListViewName() {
        return "questions/list";
    }

    @Override
    protected String getDetailViewName() {
        return "questions/view";
    }

    @Override
    protected String getListPageTitle() {
        return "Interview Questions";
    }

    @Override
    protected String getDetailPageTitle() {
        return "Question Details";
    }

    @Override
    protected String getEntityName() {
        return "Question";
    }

    @Override
    public String view(@PathVariable UUID id, Model model) {
        super.view(id, model);

        return "questions/view";
    }


    @Override
    public String list(Model model) {
        String viewName = super.list(model);

        questionFormService.prepareGenerateQuestionsForm(model);

        return viewName;
    }

    /**
     * Display the study dashboard with progress tracking information.
     * This method has been refactored to use the DashboardService, following the Single Responsibility Principle.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        dashboardViewService.prepareDashboardModel(model);
        dashboardViewService.setDashboardPageTitle(model);
        return "questions/main";
    }


    // View-only controller: Create, Edit, and Delete operations have been removed

    /**
     * Filter questions by tag.
     *
     * @param tagSlug the slug of the tag to filter by
     * @param model   the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/tag/{tagSlug}")
    public String getQuestionsByTag(@PathVariable String tagSlug, Model model) {
        // Delegate to the tag question service
        Tag tag = tagQuestionService.prepareQuestionsByTagModel(tagSlug, model);
        tagQuestionService.setQuestionsByTagPageTitle(model, tag.getName());

        // Add tags to the model for the question generation modal
        questionFormService.prepareGenerateQuestionsForm(model);

        return "questions/list";
    }

    /**
     * Search for questions by text and filter by difficulty and tag.
     * This method has been refactored to use the SearchService, following the Single Responsibility Principle.
     *
     * @param query      the search query
     * @param difficulty the difficulty to filter by
     * @param tagId      the tag ID to filter by
     * @param page       the page number
     * @param size       the page size
     * @param model      the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/search")
    public String searchQuestions(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) UUID tagId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Delegate to the search view service
        searchViewService.prepareSearchResultsModel(query, difficulty, tagId, page, size, model);
        searchViewService.setSearchResultsPageTitle(model, query, difficulty, tagId);

        // Add tags to the model for the question generation modal
        questionFormService.prepareGenerateQuestionsForm(model);

        return "questions/list";
    }

    /**
     * Generate interview questions using AI based on a tag.
     * This method handles both AJAX and regular form submissions.
     *
     * @param tagName    the name of the tag to generate questions for
     * @param count      the number of questions to generate
     * @param difficulty the difficulty level of the questions
     * @param model      the model to add attributes to
     * @return a redirect to the question list filtered by the generated tag
     */
    @PostMapping("/generate")
    public String generateQuestions(
            @RequestParam String tagName,
            @RequestParam(defaultValue = "5") int count,
            @RequestParam(defaultValue = "Medium") String difficulty,
            Model model) {

        // This operation is disabled to make the controller view-only
        model.addAttribute("error", "Question generation is disabled. This controller is view-only.");
        return "redirect:/questions";
    }

    /**
     * Generate interview questions using AI based on a tag (AJAX version).
     * This endpoint is used for AJAX requests from the modal form.
     *
     * @param tagName    the name of the tag to generate questions for
     * @param count      the number of questions to generate
     * @param difficulty the difficulty level of the questions
     * @return a JSON response with status and redirect information
     */
    @PostMapping("/api/generate")
    @ResponseBody
    public Map<String, Object> generateQuestionsApi(
            @RequestParam String tagName,
            @RequestParam(defaultValue = "5") int count,
            @RequestParam(defaultValue = "Medium") String difficulty) {

        Map<String, Object> response = new HashMap<>();

        // Validate input
        if (!questionGenerationOrchestrationService.validateTagName(tagName)) {
            response.put("success", false);
            response.put("message", "Tag name is required");
            return response;
        }

        // Start the asynchronous generation process without blocking
        questionGenerationOrchestrationService.startQuestionGeneration(tagName, count, difficulty);

        // Success message
        String successMessage = questionGenerationOrchestrationService.buildGenerationSuccessMessage(count, difficulty, tagName);

        // Find the tag to redirect to its questions page
        String redirectUrl = "/questions";
        Optional<Tag> tag = questionGenerationOrchestrationService.findTagByName(tagName);
        if (tag.isPresent()) {
            redirectUrl = "/questions/tag/" + tag.get().getSlug();
        }

        response.put("success", true);
        response.put("message", successMessage);
        response.put("redirectUrl", redirectUrl);
        return response;
    }

    /**
     * Redirect to the questions list page and show the generate questions modal.
     * This method is kept for backward compatibility with existing links.
     *
     * @param model the model to add attributes to
     * @return a redirect to the questions list page
     */
    @GetMapping("/generate")
    public String showGenerateQuestionsForm(Model model) {
        // Redirect to the questions list page where the modal will be shown
        return "redirect:/questions?showGenerateModal=true";
    }

    /**
     * Display the form for auto-generating easy questions using AI.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/auto-generate")
    public String showAutoGenerateQuestionsForm(Model model) {
        questionFormService.prepareAutoGenerateQuestionsForm(model);
        questionFormService.setAutoGenerateQuestionsPageTitle(model);
        return "questions/auto-generate";
    }

    /**
     * Display the form for auto-generating easy questions for multiple tags using AI.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/auto-generate-multi")
    public String showMultiTagAutoGenerateQuestionsForm(Model model) {
        questionFormService.prepareAutoGenerateQuestionsForm(model);
        model.addAttribute("pageTitle", "Auto-Generate Easy Questions for Multiple Tags with AI");
        return "questions/auto-generate-multi";
    }

    /**
     * Auto-generate 3 easy interview questions for a specific tag using OpenAI.
     * This endpoint processes one tag per operation and makes one request to OpenAI per tag.
     *
     * @param tagName the name of the tag to generate questions for
     * @param model   the model to add attributes to
     * @return a redirect to the question list filtered by the generated tag
     */
    @PostMapping("/auto-generate")
    public String autoGenerateEasyQuestions(
            @RequestParam String tagName,
            Model model) {

        // Validate input
        if (!questionGenerationOrchestrationService.validateTagName(tagName)) {
            model.addAttribute("error", "Tag name is required");
            return "redirect:/questions";
        }

        // Start the asynchronous generation process without blocking
        questionGenerationOrchestrationService.startEasyQuestionGeneration(tagName);

        // Add message that generation has started
        model.addAttribute("success",
                questionGenerationOrchestrationService.buildEasyGenerationSuccessMessage(tagName));

        // Find the tag to redirect to its questions page
        Optional<Tag> tag = questionGenerationOrchestrationService.findTagByName(tagName);
        if (tag.isPresent()) {
            return "redirect:/questions/tag/" + tag.get().getSlug();
        } else {
            return "redirect:/questions";
        }
    }

    /**
     * REST endpoint to auto-generate 3 easy interview questions for a specific tag using OpenAI.
     * This endpoint processes one tag per operation and makes one request to OpenAI per tag.
     *
     * @param tagName the name of the tag to generate questions for
     * @return a map containing the status of the operation
     */
    @PostMapping("/api/auto-generate")
    @ResponseBody
    public Map<String, Object> apiAutoGenerateEasyQuestions(@RequestParam String tagName) {
        // Validate input
        if (!questionGenerationOrchestrationService.validateTagName(tagName)) {
            return questionGenerationOrchestrationService.buildApiResponse(false, "Tag name is required");
        }

        logger.info("API: Starting asynchronous generation of 3 easy questions for tag: {}", tagName);

        // Start the asynchronous generation process without blocking
        questionGenerationOrchestrationService.startEasyQuestionGeneration(tagName);

        return questionGenerationOrchestrationService.buildApiResponse(true,
                String.format("Started auto-generating 3 easy questions for tag '%s'", tagName));
    }

    /**
     * Auto-generate 3 easy interview questions for multiple tags using OpenAI.
     * This endpoint processes each tag separately and makes one request to OpenAI per tag.
     *
     * @param tagIds the IDs of the tags to generate questions for
     * @param model  the model to add attributes to
     * @return a redirect to the question list
     */
    @PostMapping("/auto-generate-multi")
    public String autoGenerateEasyQuestionsForMultipleTags(
            @RequestParam(value = "tagIds", required = false) List<UUID> tagIds,
            Model model) {

        // Validate input
        if (tagIds == null || tagIds.isEmpty()) {
            model.addAttribute("error", "At least one tag must be selected");
            return "redirect:/questions/auto-generate-multi";
        }

        logger.info("Starting asynchronous generation of easy questions for {} tags", tagIds.size());

        // Start the asynchronous generation process without blocking
        questionGenerationOrchestrationService.startEasyQuestionGenerationForMultipleTags(tagIds);

        // Add message that generation has started
        model.addAttribute("success",
                questionGenerationOrchestrationService.buildMultiTagEasyGenerationSuccessMessage(tagIds.size()));

        return "redirect:/questions";
    }
}
