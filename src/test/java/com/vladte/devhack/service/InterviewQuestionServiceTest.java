package com.vladte.devhack.service;

import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.Tag;
import com.vladte.devhack.repository.InterviewQuestionRepository;
import com.vladte.devhack.service.domain.impl.InterviewQuestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InterviewQuestionServiceTest {

    @Mock
    private InterviewQuestionRepository questionRepository;

    @InjectMocks
    private InterviewQuestionServiceImpl questionService;

    private InterviewQuestion testQuestion;
    private Tag testTag;
    private List<InterviewQuestion> testQuestions;
    private final UUID TEST_TAG_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID TEST_QUESTION_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test tag
        testTag = new Tag();
        testTag.setId(TEST_TAG_ID);
        testTag.setName("Test Tag");
        testTag.setQuestions(new HashSet<>());

        // Create test question
        testQuestion = new InterviewQuestion();
        testQuestion.setId(TEST_QUESTION_ID);
        testQuestion.setQuestionText("Test Question");
        testQuestion.setDifficulty("Medium");
        testQuestion.setTags(new HashSet<>());
        testQuestion.getTags().add(testTag);

        testQuestions = new ArrayList<>();
        testQuestions.add(testQuestion);
    }

    @Test
    public void testSave() {
        when(questionRepository.save(any(InterviewQuestion.class))).thenReturn(testQuestion);

        InterviewQuestion savedQuestion = questionService.save(testQuestion);

        assertEquals(testQuestion, savedQuestion);
        verify(questionRepository, times(1)).save(testQuestion);
    }

    @Test
    public void testFindAll() {
        when(questionRepository.findAll()).thenReturn(testQuestions);

        List<InterviewQuestion> foundQuestions = questionService.findAll();

        assertEquals(testQuestions, foundQuestions);
        verify(questionRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(questionRepository.findById(TEST_QUESTION_ID)).thenReturn(Optional.of(testQuestion));

        Optional<InterviewQuestion> foundQuestion = questionService.findById(TEST_QUESTION_ID);

        assertTrue(foundQuestion.isPresent());
        assertEquals(testQuestion, foundQuestion.get());
        verify(questionRepository, times(1)).findById(TEST_QUESTION_ID);
    }

    @Test
    public void testDeleteById() {
        doNothing().when(questionRepository).deleteById(TEST_QUESTION_ID);

        questionService.deleteById(TEST_QUESTION_ID);

        verify(questionRepository, times(1)).deleteById(TEST_QUESTION_ID);
    }

    @Test
    public void testFindQuestionsByDifficulty() {
        when(questionRepository.findByDifficulty("Medium")).thenReturn(testQuestions);

        List<InterviewQuestion> foundQuestions = questionService.findQuestionsByDifficulty("Medium");

        assertEquals(testQuestions, foundQuestions);
        verify(questionRepository, times(1)).findByDifficulty("Medium");
    }

    @Test
    public void testFindQuestionsByTag() {
        when(questionRepository.findByTagsContaining(testTag)).thenReturn(testQuestions);

        List<InterviewQuestion> foundQuestions = questionService.findQuestionsByTag(testTag);

        assertEquals(testQuestions, foundQuestions);
        verify(questionRepository, times(1)).findByTagsContaining(testTag);
    }

    @Test
    public void testFindQuestionsByDifficultyAndTag() {
        when(questionRepository.findByDifficultyAndTagsContaining("Medium", testTag)).thenReturn(testQuestions);

        List<InterviewQuestion> foundQuestions = questionService.findQuestionsByDifficultyAndTag("Medium", testTag);

        assertEquals(testQuestions, foundQuestions);
        verify(questionRepository, times(1)).findByDifficultyAndTagsContaining("Medium", testTag);
    }
}
