package com.vladte.devhack.controller;

import com.vladte.devhack.dto.AnswerDTO;
import com.vladte.devhack.mapper.AnswerMapper;
import com.vladte.devhack.model.Answer;
import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.User;
import com.vladte.devhack.service.domain.AnswerService;
import com.vladte.devhack.service.domain.InterviewQuestionService;
import com.vladte.devhack.service.domain.UserService;
import com.vladte.devhack.service.view.AnswerFormService;
import com.vladte.devhack.service.view.ModelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/answers")
public class AnswerController extends UserEntityController<Answer, UUID, AnswerService> {

    private final InterviewQuestionService questionService;
    private final AnswerFormService answerFormService;
    private final AnswerMapper answerMapper;

    @Autowired
    public AnswerController(AnswerService answerService,
                            UserService userService,
                            InterviewQuestionService questionService,
                            AnswerFormService answerFormService,
                            AnswerMapper answerMapper) {
        super(answerService, userService);
        this.questionService = questionService;
        this.answerFormService = answerFormService;
        this.answerMapper = answerMapper;
    }

    @Override
    protected User getEntityUser(Answer entity) {
        return entity.getUser();
    }

    @Override
    protected String getListViewName() {
        return "answers/list";
    }

    @Override
    protected String getDetailViewName() {
        return "answers/view";
    }

    @Override
    protected String getListPageTitle() {
        return "Answers";
    }

    @Override
    protected String getDetailPageTitle() {
        return "Answer Details";
    }

    @Override
    protected String getEntityName() {
        return "Answer";
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        // Get the current authenticated user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        // Find the user by email
        User currentUser = userService.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Get answers for the current user with pagination
        Page<Answer> answerPage = service.findAnswersByUser(currentUser, pageable);

        // Convert to DTO page
        Page<AnswerDTO> answerDTOPage = answerPage.map(answerMapper::toDTO);

        // Using the new ModelBuilder to build the model with pagination data
        ModelBuilder.of(model)
                .addPagination(answerDTOPage, page, size, "answers")
                .setPageTitle("My Answers")
                .build();

        return "answers/list";
    }


    @GetMapping("/new")
    public String newAnswerForm(@RequestParam(required = false) UUID questionId, Model model) {
        // Delegate to the form service
        answerFormService.prepareNewAnswerForm(questionId, model);
        answerFormService.setNewAnswerPageTitle(model);
        return "answers/form";
    }

    /**
     * Display the form for editing an existing answer.
     *
     * @param id    the ID of the answer to edit
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/{id}")
    public String view(@PathVariable UUID id, Model model) {
        // Delegate to the form service
        AnswerDTO answerDTO = answerFormService.prepareEditAnswerForm(id, model);
        if (answerDTO == null) {
            throw new IllegalArgumentException("Answer not found");
        }

        // Add the question to the model for the view
        if (answerDTO.getQuestionId() != null) {
            questionService.findById(answerDTO.getQuestionId())
                    .ifPresent(question -> model.addAttribute("question", question));
        }

        answerFormService.setEditAnswerPageTitle(model);
        return "answers/view";
    }


    /**
     * Display the form for editing an existing answer.
     *
     * @param id    the ID of the answer to edit
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/{id}/edit")
    public String editAnswerForm(@PathVariable UUID id, Model model) {
        // Delegate to the form service
        if (answerFormService.prepareEditAnswerForm(id, model) == null) {
            throw new IllegalArgumentException("Answer not found");
        }
        answerFormService.setEditAnswerPageTitle(model);
        return "answers/form";
    }

    /**
     * Process the form submission for creating or updating an answer.
     *
     * @param answerDTO  the answer data from the form
     * @param userId     the ID of the user who created the answer
     * @param questionId the ID of the question being answered
     * @return a redirect to the answer list
     */
    @PostMapping
    public String saveAnswer(
            @ModelAttribute AnswerDTO answerDTO,
            @RequestParam UUID userId,
            @RequestParam UUID questionId) {
        answerFormService.saveAnswer(answerDTO, userId, questionId);
        return "redirect:/answers";
    }

    /**
     * Delete an answer.
     *
     * @param id the ID of the answer to delete
     * @return a redirect to the answer list
     */
    @GetMapping("/{id}/delete")
    public String deleteAnswer(@PathVariable UUID id) {
        service.deleteById(id);
        return "redirect:/answers";
    }

    /**
     * Display answers by user.
     *
     * @param userId the ID of the user
     * @param model  the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/user/{userId}")
    public String getAnswersByUser(@PathVariable UUID userId, Model model) {
        User user = getEntityOrThrow(userService.findById(userId), "User not found");
        List<Answer> answers = service.findAnswersByUser(user);

        // Convert to DTO list
        List<AnswerDTO> answerDTOs = answers.stream()
                .map(answerMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());

        // Using ModelBuilder to build the model
        ModelBuilder.of(model)
                .addAttribute("answers", answerDTOs)
                .addAttribute("user", user)
                .setPageTitle("Answers by " + user.getName())
                .build();

        return "answers/list";
    }

    /**
     * Display answers by question.
     *
     * @param questionId the ID of the question
     * @param model      the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/question/{questionId}")
    public String getAnswersByQuestion(@PathVariable UUID questionId, Model model) {
        InterviewQuestion question = getEntityOrThrow(questionService.findById(questionId), "Question not found");
        List<Answer> answers = service.findAnswersByQuestion(question);

        // Convert to DTO list
        List<AnswerDTO> answerDTOs = answers.stream()
                .map(answerMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());

        // Using the new ModelBuilder to build the model
        ModelBuilder.of(model)
                .addAttribute("answers", answerDTOs)
                .addAttribute("question", question)
                .setPageTitle("Answers for Question: " + question.getQuestionText())
                .build();

        return "answers/list";
    }

    /**
     * Check an answer using AI and update its score and feedback.
     *
     * @param id    the ID of the answer to check
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/{id}/check")
    public String checkAnswerWithAi(@PathVariable UUID id, Model model) {
        // Delegate to the form service
        com.vladte.devhack.dto.AnswerDTO answerDTO = answerFormService.checkAnswerWithAi(id);
        if (answerDTO == null) {
            throw new IllegalArgumentException("Answer not found");
        }

        // Add additional information to the model
        ModelBuilder.of(model)
                .addAttribute("message", "Answer checked by AI. Score: " + answerDTO.getAiScore())
                .addAttribute("aiFeedback", answerDTO.getAiFeedback())
                .build();

        // Set the page title
        answerFormService.setEditAnswerPageTitle(model);

        return "answers/view";
    }
}
