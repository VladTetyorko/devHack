//package com.vladte.devhack.controller;
//
//import com.vladte.devhack.model.InterviewQuestion;
//import com.vladte.devhack.model.Note;
//import com.vladte.devhack.model.User;
//import com.vladte.devhack.service.domain.InterviewQuestionService;
//import com.vladte.devhack.service.domain.NoteService;
//import com.vladte.devhack.service.domain.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.ui.Model;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//public class NoteControllerTest {
//
//    @Mock
//    private NoteService noteService;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private InterviewQuestionService questionService;
//
//    @Mock
//    private Model model;
//
//    @InjectMocks
//    private NoteController noteController;
//
//    private MockMvc mockMvc;
//
//    private User testUser;
//    private InterviewQuestion testQuestion;
//    private Note testNote;
//    private List<Note> testNotes;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(noteController).build();
//
//        // Create test data
//        testUser = new User();
//        testUser.setId(1L);
//        testUser.setName("Test User");
//        testUser.setEmail("test@example.com");
//        testUser.setPassword("password");
//
//        testQuestion = new InterviewQuestion();
//        testQuestion.setId(1L);
//        testQuestion.setQuestionText("Test Question");
//        testQuestion.setDifficulty("Medium");
//
//        testNote = new Note();
//        testNote.setId(1L);
//        testNote.setUser(testUser);
//        testNote.setQuestion(testQuestion);
//        testNote.setNoteText("Test Note Text");
//        testNote.setCreatedAt(LocalDateTime.now());
//        testNote.setUpdatedAt(LocalDateTime.now());
//
//        testNotes = new ArrayList<>();
//        testNotes.add(testNote);
//    }
//
//    @Test
//    public void testList() throws Exception {
//        when(noteService.findAll()).thenReturn(testNotes);
//
//        mockMvc.perform(get("/notes"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("notes/list"))
//                .andExpect(model().attributeExists("notes"))
//                .andExpect(model().attribute("notes", testNotes));
//
//        verify(noteService, times(1)).findAll();
//    }
//
//    @Test
//    public void testView() throws Exception {
//        when(noteService.findById(1L)).thenReturn(Optional.of(testNote));
//
//        mockMvc.perform(get("/notes/1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("notes/view"))
//                .andExpect(model().attributeExists("note"))
//                .andExpect(model().attribute("note", testNote));
//
//        verify(noteService, times(1)).findById(1L);
//    }
//
//    @Test
//    public void testNewNoteForm() throws Exception {
//        when(userService.findAll()).thenReturn(List.of(testUser));
//        when(questionService.findAll()).thenReturn(List.of(testQuestion));
//
//        mockMvc.perform(get("/notes/new"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("notes/form"))
//                .andExpect(model().attributeExists("note"))
//                .andExpect(model().attributeExists("users"))
//                .andExpect(model().attributeExists("questions"));
//
//        verify(userService, times(1)).findAll();
//        verify(questionService, times(1)).findAll();
//    }
//
//    @Test
//    public void testNewNoteFormWithQuestionId() throws Exception {
//        when(questionService.findById(1L)).thenReturn(Optional.of(testQuestion));
//        when(userService.findAll()).thenReturn(List.of(testUser));
//        when(questionService.findAll()).thenReturn(List.of(testQuestion));
//
//        mockMvc.perform(get("/notes/new").param("questionId", "1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("notes/form"))
//                .andExpect(model().attributeExists("note"))
//                .andExpect(model().attributeExists("question"))
//                .andExpect(model().attributeExists("users"))
//                .andExpect(model().attributeExists("questions"));
//
//        verify(questionService, times(1)).findById(1L);
//        verify(userService, times(1)).findAll();
//        verify(questionService, times(1)).findAll();
//    }
//
//    @Test
//    public void testEditNoteForm() throws Exception {
//        when(noteService.findById(1L)).thenReturn(Optional.of(testNote));
//        when(userService.findAll()).thenReturn(List.of(testUser));
//        when(questionService.findAll()).thenReturn(List.of(testQuestion));
//
//        mockMvc.perform(get("/notes/1/edit"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("notes/form"))
//                .andExpect(model().attributeExists("note"))
//                .andExpect(model().attributeExists("users"))
//                .andExpect(model().attributeExists("questions"));
//
//        verify(noteService, times(1)).findById(1L);
//        verify(userService, times(1)).findAll();
//        verify(questionService, times(1)).findAll();
//    }
//
//    @Test
//    public void testSaveNote() throws Exception {
//        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
//        when(questionService.findById(1L)).thenReturn(Optional.of(testQuestion));
//        when(noteService.save(any(Note.class))).thenReturn(testNote);
//
//        mockMvc.perform(post("/notes")
//                .param("noteText", "Test Note Text")
//                .param("userId", "1")
//                .param("questionId", "1"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/notes"));
//
//        verify(userService, times(1)).findById(1L);
//        verify(questionService, times(1)).findById(1L);
//        verify(noteService, times(1)).save(any(Note.class));
//    }
//
//    @Test
//    public void testDeleteNote() throws Exception {
//        mockMvc.perform(get("/notes/1/delete"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/notes"));
//
//        verify(noteService, times(1)).deleteById(1L);
//    }
//
//    @Test
//    public void testGetNotesByUser() throws Exception {
//        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
//        when(noteService.findNotesByUser(testUser)).thenReturn(testNotes);
//
//        mockMvc.perform(get("/notes/user/1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("notes/list"))
//                .andExpect(model().attributeExists("notes"))
//                .andExpect(model().attributeExists("user"))
//                .andExpect(model().attribute("notes", testNotes))
//                .andExpect(model().attribute("user", testUser));
//
//        verify(userService, times(1)).findById(1L);
//        verify(noteService, times(1)).findNotesByUser(testUser);
//    }
//
//    @Test
//    public void testGetNotesByQuestion() throws Exception {
//        when(questionService.findById(1L)).thenReturn(Optional.of(testQuestion));
//        when(noteService.findNotesByLinkedQuestion(testQuestion)).thenReturn(testNotes);
//
//        mockMvc.perform(get("/notes/question/1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("notes/list"))
//                .andExpect(model().attributeExists("notes"))
//                .andExpect(model().attributeExists("question"))
//                .andExpect(model().attribute("notes", testNotes))
//                .andExpect(model().attribute("question", testQuestion));
//
//        verify(questionService, times(1)).findById(1L);
//        verify(noteService, times(1)).findNotesByLinkedQuestion(testQuestion);
//    }
//}
