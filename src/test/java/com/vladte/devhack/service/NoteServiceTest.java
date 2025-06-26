//package com.vladte.devhack.service;
//
//import com.vladte.devhack.model.InterviewQuestion;
//import com.vladte.devhack.model.Note;
//import com.vladte.devhack.model.User;
//import com.vladte.devhack.repository.NoteRepository;
//import com.vladte.devhack.service.domain.impl.NoteServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class NoteServiceTest {
//
//    @Mock
//    private NoteRepository noteRepository;
//
//    @InjectMocks
//    private NoteServiceImpl noteService;
//
//    private User testUser;
//    private InterviewQuestion testQuestion;
//    private Note testNote;
//    private List<Note> testNotes;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//
//        // Create test data
//        testUser = new User();
//        testUser.setId(UUID.randomUUID());
//        testUser.setName("Test User");
//        testUser.setEmail("test@example.com");
//        testUser.setPassword("password");
//
//        testQuestion = new InterviewQuestion();
//        testQuestion.setId(UUID.randomUUID());
//        testQuestion.setQuestionText("Test Question");
//        testQuestion.setDifficulty("Medium");
//
//        testNote = new Note();
//        testNote.setId(UUID.randomUUID());
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
//    public void testSave() {
//        when(noteRepository.save(any(Note.class))).thenReturn(testNote);
//
//        Note savedNote = noteService.save(testNote);
//
//        assertEquals(testNote, savedNote);
//        verify(noteRepository, times(1)).save(testNote);
//    }
//
//    @Test
//    public void testFindAll() {
//        when(noteRepository.findAll()).thenReturn(testNotes);
//
//        List<Note> foundNotes = noteService.findAll();
//
//        assertEquals(testNotes, foundNotes);
//        verify(noteRepository, times(1)).findAll();
//    }
//
//    @Test
//    public void testFindById() {
//        when(noteRepository.findById(UUID.randomUUID())).thenReturn(Optional.of(testNote));
//
//        Optional<Note> foundNote = noteService.findById(UUID.randomUUID());
//
//        assertTrue(foundNote.isPresent());
//        assertEquals(testNote, foundNote.get());
//        verify(noteRepository, times(1)).findById(UUID.randomUUID());
//    }
//
//    @Test
//    public void testDeleteById() {
//        doNothing().when(noteRepository).deleteById(UUID.randomUUID());
//
//        noteService.deleteById(UUID.randomUUID());
//
//        verify(noteRepository, times(1)).deleteById(UUID.randomUUID());
//    }
//
//    @Test
//    public void testFindNotesByUser() {
//        when(noteRepository.findByUser(testUser)).thenReturn(testNotes);
//
//        List<Note> foundNotes = noteService.findNotesByUser(testUser);
//
//        assertEquals(testNotes, foundNotes);
//        verify(noteRepository, times(1)).findByUser(testUser);
//    }
//
//    @Test
//    public void testFindNotesByLinkedQuestion() {
//        when(noteRepository.findByQuestion(testQuestion)).thenReturn(testNotes);
//
//        List<Note> foundNotes = noteService.findNotesByLinkedQuestion(testQuestion);
//
//        assertEquals(testNotes, foundNotes);
//        verify(noteRepository, times(1)).findByQuestion(testQuestion);
//    }
//
//    @Test
//    public void testFindNotesByUserAndLinkedQuestion() {
//        when(noteRepository.findByUserAndQuestion(testUser, testQuestion)).thenReturn(testNotes);
//
//        List<Note> foundNotes = noteService.findNotesByUserAndLinkedQuestion(testUser, testQuestion);
//
//        assertEquals(testNotes, foundNotes);
//        verify(noteRepository, times(1)).findByUserAndQuestion(testUser, testQuestion);
//    }
//}