package com.vladte.devhack.controller;

import com.vladte.devhack.model.Answer;
import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.User;
import com.vladte.devhack.service.domain.AnswerService;
import com.vladte.devhack.service.domain.InterviewQuestionService;
import com.vladte.devhack.service.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for handling requests related to answers.
 */
@Controller
@RequestMapping("/answers")
public class AnswerController extends BaseCrudController<Answer, UUID, AnswerService> {

    private final UserService userService;
    private final InterviewQuestionService questionService;

    @Autowired
    public AnswerController(AnswerService answerService, UserService userService, InterviewQuestionService questionService) {
        super(answerService);
        this.userService = userService;
        this.questionService = questionService;
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

    /**
     * Display a list of all answers.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @Override
    public String list(Model model) {
        List<Answer> answers = service.findAll();
        model.addAttribute("answers", answers);
        setPageTitle(model, "Answers");
        return "answers/list";
    }


    /**
     * Display the form for creating a new answer.
     *
     * @param questionId the ID of the question being answered
     * @param model      the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/new")
    public String newAnswerForm(@RequestParam(required = false) UUID questionId, Model model) {
        Answer answer = new Answer();

        if (questionId != null) {
            Optional<InterviewQuestion> questionOpt = questionService.findById(questionId);
            questionOpt.ifPresent(question -> {
                answer.setQuestion(question);
                model.addAttribute("question", question);
            });
        }

        model.addAttribute("answer", answer);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("questions", questionService.findAll());
        setPageTitle(model, "Create New Answer");
        return "answers/form";
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
        Answer answer = getEntityOrThrow(service.findById(id), "Answer not found");
        model.addAttribute("answer", answer);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("questions", questionService.findAll());
        setPageTitle(model, "Edit Answer");
        return "answers/form";
    }

    /**
     * Process the form submission for creating or updating an answer.
     *
     * @param answer     the answer data from the form
     * @param userId     the ID of the user who created the answer
     * @param questionId the ID of the question being answered
     * @return a redirect to the answer list
     */
    @PostMapping
    public String saveAnswer(
            @ModelAttribute Answer answer,
            @RequestParam UUID userId,
            @RequestParam UUID questionId) {
        User user = getEntityOrThrow(userService.findById(userId), "User not found");
        InterviewQuestion question = getEntityOrThrow(questionService.findById(questionId), "Question not found");
        answer.setUser(user);
        answer.setQuestion(question);

        service.save(answer);
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
        model.addAttribute("answers", answers);
        model.addAttribute("user", user);
        setPageTitle(model, "Answers by " + user.getName());
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
        model.addAttribute("answers", answers);
        model.addAttribute("question", question);
        setPageTitle(model, "Answers for Question: " + question.getQuestionText());
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
        Answer answer = service.checkAnswerWithAi(id);
        model.addAttribute("answer", answer);
        model.addAttribute("message", "Answer checked by AI. Score: " + answer.getAiScore());
        model.addAttribute("aiFeedback", answer.getAiFeedback());
        setPageTitle(model, "Answer Details");
        return "answers/view";
    }
}
