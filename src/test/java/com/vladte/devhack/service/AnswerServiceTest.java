package com.vladte.devhack.service;

import com.vladte.devhack.model.Answer;
import com.vladte.devhack.model.InterviewQuestion;
import com.vladte.devhack.model.User;
import com.vladte.devhack.repository.AnswerRepository;
import com.vladte.devhack.service.api.OpenAiService;
import com.vladte.devhack.service.domain.impl.AnswerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private OpenAiService openAiService;

    @InjectMocks
    private AnswerServiceImpl answerService;

    private User testUser;
    private InterviewQuestion testQuestion;
    private Answer testAnswer;
    private List<Answer> testAnswers;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");

        // Create test question
        testQuestion = new InterviewQuestion();
        testQuestion.setId(UUID.randomUUID());
        testQuestion.setQuestionText("Test Question");
        testQuestion.setDifficulty("Medium");

        // Create test answer
        testAnswer = new Answer();
        testAnswer.setId(UUID.randomUUID());
        testAnswer.setText("Test Answer");
        testAnswer.setUser(testUser);
        testAnswer.setQuestion(testQuestion);

        testAnswers = new ArrayList<>();
        testAnswers.add(testAnswer);
    }

    @Test
    public void testSave() {
        when(answerRepository.save(any(Answer.class))).thenReturn(testAnswer);

        Answer savedAnswer = answerService.save(testAnswer);

        assertEquals(testAnswer, savedAnswer);
        verify(answerRepository, times(1)).save(testAnswer);
    }

    @Test
    public void testFindAll() {
        when(answerRepository.findAll()).thenReturn(testAnswers);

        List<Answer> foundAnswers = answerService.findAll();

        assertEquals(testAnswers, foundAnswers);
        verify(answerRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(answerRepository.findById(UUID.randomUUID())).thenReturn(Optional.of(testAnswer));

        Optional<Answer> foundAnswer = answerService.findById(UUID.randomUUID());

        assertTrue(foundAnswer.isPresent());
        assertEquals(testAnswer, foundAnswer.get());
        verify(answerRepository, times(1)).findById(UUID.randomUUID());
    }

    @Test
    public void testDeleteById() {
        doNothing().when(answerRepository).deleteById(UUID.randomUUID());

        answerService.deleteById(UUID.randomUUID());

        verify(answerRepository, times(1)).deleteById(UUID.randomUUID());
    }

    @Test
    public void testFindAnswersByUser() {
        when(answerRepository.findByUser(testUser)).thenReturn(testAnswers);

        List<Answer> foundAnswers = answerService.findAnswersByUser(testUser);

        assertEquals(testAnswers, foundAnswers);
        verify(answerRepository, times(1)).findByUser(testUser);
    }

    @Test
    public void testFindAnswersByQuestion() {
        when(answerRepository.findByQuestion(testQuestion)).thenReturn(testAnswers);

        List<Answer> foundAnswers = answerService.findAnswersByQuestion(testQuestion);

        assertEquals(testAnswers, foundAnswers);
        verify(answerRepository, times(1)).findByQuestion(testQuestion);
    }

    @Test
    public void testFindAnswersByUserAndQuestion() {
        when(answerRepository.findByUserAndQuestion(testUser, testQuestion)).thenReturn(testAnswers);

        List<Answer> foundAnswers = answerService.findAnswersByUserAndQuestion(testUser, testQuestion);

        assertEquals(testAnswers, foundAnswers);
        verify(answerRepository, times(1)).findByUserAndQuestion(testUser, testQuestion);
    }

    @Test
    public void testCheckAnswerWithAi() {
        // Set up test data
        Double expectedScore = 85.5;
        String expectedFeedback = "This is a good answer, but it could be improved by...";

        // Create a map with the expected score and feedback
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("score", expectedScore);
        resultMap.put("feedback", expectedFeedback);

        // Mock the OpenAiService to return the expected map
        when(openAiService.checkAnswerWithFeedback(anyString(), anyString())).thenReturn(resultMap);

        // Mock the repository to return the test answer when findById is called
        when(answerRepository.findById(UUID.randomUUID())).thenReturn(Optional.of(testAnswer));

        // Mock the repository to return the updated answer when save is called
        Answer updatedAnswer = new Answer();
        updatedAnswer.setId(UUID.randomUUID());
        updatedAnswer.setText("Test Answer");
        updatedAnswer.setUser(testUser);
        updatedAnswer.setQuestion(testQuestion);
        updatedAnswer.setAiScore(expectedScore);
        updatedAnswer.setAiFeedback(expectedFeedback);
        when(answerRepository.save(any(Answer.class))).thenReturn(updatedAnswer);

        // Call the method being tested
        Answer result = answerService.checkAnswerWithAi(UUID.randomUUID());

        // Verify the result
        assertEquals(expectedScore, result.getAiScore());
        assertEquals(expectedFeedback, result.getAiFeedback());

        // Verify that the OpenAiService was called with the correct parameters
        verify(openAiService, times(1)).checkAnswerWithFeedback(testQuestion.getQuestionText(), testAnswer.getText());

        // Verify that the repository's findById and save methods were called
        verify(answerRepository, times(1)).findById(UUID.randomUUID());
        verify(answerRepository, times(1)).save(any(Answer.class));
    }
}
