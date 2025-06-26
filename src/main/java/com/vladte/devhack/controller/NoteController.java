package com.vladte.devhack.controller;

import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.Note;
import com.vladte.devhack.model.User;
import com.vladte.devhack.service.domain.InterviewQuestionService;
import com.vladte.devhack.service.domain.NoteService;
import com.vladte.devhack.service.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for handling requests related to notes.
 */
@Controller
@RequestMapping("/notes")
public class NoteController extends BaseCrudController<Note, UUID, NoteService> {

    private final UserService userService;
    private final InterviewQuestionService questionService;

    @Autowired
    public NoteController(NoteService noteService, UserService userService, InterviewQuestionService questionService) {
        super(noteService);
        this.userService = userService;
        this.questionService = questionService;
    }

    @Override
    protected String getListViewName() {
        return "notes/list";
    }

    @Override
    protected String getDetailViewName() {
        return "notes/view";
    }

    @Override
    protected String getListPageTitle() {
        return "Notes";
    }

    @Override
    protected String getDetailPageTitle() {
        return "Note Details";
    }

    @Override
    protected String getEntityName() {
        return "Note";
    }

    /**
     * Display a list of all notes.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @Override
    public String list(Model model) {
        List<Note> notes = service.findAll();
        model.addAttribute("notes", notes);
        setPageTitle(model, "Notes");
        return "notes/list";
    }

    /**
     * Display a specific note.
     *
     * @param id the ID of the note to display
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @Override
    public String view(@PathVariable UUID id, Model model) {
        Note note = getEntityOrThrow(service.findById(id), "Note not found");
        model.addAttribute("note", note);
        setPageTitle(model, "Note Details");
        return "notes/view";
    }

    /**
     * Display the form for creating a new note.
     *
     * @param questionId the ID of the question being linked to the note
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/new")
    public String newNoteForm(@RequestParam(required = false) UUID questionId, Model model) {
        Note note = new Note();

        if (questionId != null) {
            Optional<InterviewQuestion> questionOpt = questionService.findById(questionId);
            questionOpt.ifPresent(question -> {
                note.setQuestion(question);
                model.addAttribute("question", question);
            });
        }

        model.addAttribute("note", note);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("questions", questionService.findAll());
        setPageTitle(model, "Create New Note");
        return "notes/form";
    }

    /**
     * Display the form for editing an existing note.
     *
     * @param id the ID of the note to edit
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/{id}/edit")
    public String editNoteForm(@PathVariable UUID id, Model model) {
        Note note = getEntityOrThrow(service.findById(id), "Note not found");
        model.addAttribute("note", note);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("questions", questionService.findAll());
        setPageTitle(model, "Edit Note");
        return "notes/form";
    }

    /**
     * Process the form submission for creating or updating a note.
     *
     * @param note the note data from the form
     * @param userId the ID of the user who created the note
     * @param questionId the ID of the question linked to the note
     * @return a redirect to the note list
     */
    @PostMapping
    public String saveNote(
            @ModelAttribute Note note,
            @RequestParam UUID userId,
            @RequestParam UUID questionId) {

        User user = getEntityOrThrow(userService.findById(userId), "User not found");
        InterviewQuestion question = getEntityOrThrow(questionService.findById(questionId), "Question not found");

        note.setUser(user);
        note.setQuestion(question);

        service.save(note);

        return "redirect:/notes";
    }

    /**
     * Delete a note.
     *
     * @param id the ID of the note to delete
     * @return a redirect to the note list
     */
    @GetMapping("/{id}/delete")
    public String deleteNote(@PathVariable UUID id) {
        service.deleteById(id);
        return "redirect:/notes";
    }

    /**
     * Display notes by user.
     *
     * @param userId the ID of the user
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/user/{userId}")
    public String getNotesByUser(@PathVariable UUID userId, Model model) {
        User user = getEntityOrThrow(userService.findById(userId), "User not found");
        List<Note> notes = ((NoteService) service).findNotesByUser(user);
        model.addAttribute("notes", notes);
        model.addAttribute("user", user);
        setPageTitle(model, "Notes by " + user.getName());
        return "notes/list";
    }

    /**
     * Display notes by question.
     *
     * @param questionId the ID of the question
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/question/{questionId}")
    public String getNotesByQuestion(@PathVariable UUID questionId, Model model) {
        InterviewQuestion question = getEntityOrThrow(questionService.findById(questionId), "Question not found");
        List<Note> notes = ((NoteService) service).findNotesByLinkedQuestion(question);
        model.addAttribute("notes", notes);
        model.addAttribute("question", question);
        setPageTitle(model, "Notes for Question: " + question.getQuestionText());
        return "notes/list";
    }
}
