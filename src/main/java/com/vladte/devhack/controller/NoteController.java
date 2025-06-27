package com.vladte.devhack.controller;

import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.Note;
import com.vladte.devhack.model.User;
import com.vladte.devhack.service.domain.InterviewQuestionService;
import com.vladte.devhack.service.domain.NoteService;
import com.vladte.devhack.service.domain.UserService;
import com.vladte.devhack.service.view.ModelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/notes")
public class NoteController extends UserEntityController<Note, UUID, NoteService> {

    private final InterviewQuestionService questionService;

    @Autowired
    public NoteController(NoteService noteService, UserService userService, InterviewQuestionService questionService) {
        super(noteService, userService);
        this.questionService = questionService;
    }

    @Override
    protected User getEntityUser(Note entity) {
        return entity.getUser();
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

    @Override
    public String view(@PathVariable UUID id, Model model) {
        Note note = getEntityOrThrow(service.findById(id), "Note not found");

        ModelBuilder.of(model)
                .addAttribute("note", note)
                .setPageTitle("Note Details")
                .build();

        return "notes/view";
    }

    @GetMapping("/new")
    public String newNoteForm(@RequestParam(required = false) UUID questionId, Model model) {
        Note note = new Note();

        // Create a ModelBuilder instance
        ModelBuilder modelBuilder = ModelBuilder.of(model)
                .addAttribute("note", note)
                .addAttribute("users", userService.findAll())
                .addAttribute("questions", questionService.findAll())
                .setPageTitle("Create New Note");

        if (questionId != null) {
            Optional<InterviewQuestion> questionOpt = questionService.findById(questionId);
            questionOpt.ifPresent(question -> {
                note.setQuestion(question);
                modelBuilder.addAttribute("question", question);
            });
        }

        modelBuilder.build();
        return "notes/form";
    }

    @GetMapping("/{id}/edit")
    public String editNoteForm(@PathVariable UUID id, Model model) {
        Note note = getEntityOrThrow(service.findById(id), "Note not found");

        ModelBuilder.of(model)
                .addAttribute("note", note)
                .addAttribute("users", userService.findAll())
                .addAttribute("questions", questionService.findAll())
                .setPageTitle("Edit Note")
                .build();

        return "notes/form";
    }

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

    @GetMapping("/{id}/delete")
    public String deleteNote(@PathVariable UUID id) {
        service.deleteById(id);
        return "redirect:/notes";
    }

    @GetMapping("/user/{userId}")
    public String getNotesByUser(@PathVariable UUID userId, Model model) {
        User user = getEntityOrThrow(userService.findById(userId), "User not found");
        List<Note> notes = service.findNotesByUser(user);

        ModelBuilder.of(model)
                .addAttribute("notes", notes)
                .addAttribute("user", user)
                .setPageTitle("Notes by " + user.getName())
                .build();

        return "notes/list";
    }

    @GetMapping("/question/{questionId}")
    public String getNotesByQuestion(@PathVariable UUID questionId, Model model) {
        InterviewQuestion question = getEntityOrThrow(questionService.findById(questionId), "Question not found");
        List<Note> notes = service.findNotesByLinkedQuestion(question);

        ModelBuilder.of(model)
                .addAttribute("notes", notes)
                .addAttribute("question", question)
                .setPageTitle("Notes for Question: " + question.getQuestionText())
                .build();

        return "notes/list";
    }
}
