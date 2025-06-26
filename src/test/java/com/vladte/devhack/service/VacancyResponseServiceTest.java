//package com.vladte.devhack.service;
//
//import com.vladte.devhack.model.InterviewStage;
//import com.vladte.devhack.model.User;
//import com.vladte.devhack.model.VacancyResponse;
//import com.vladte.devhack.repository.VacancyResponseRepository;
//import com.vladte.devhack.service.domain.impl.VacancyResponseServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class VacancyResponseServiceTest {
//
//    @Mock
//    private VacancyResponseRepository vacancyResponseRepository;
//
//    @InjectMocks
//    private VacancyResponseServiceImpl vacancyResponseService;
//
//    private User testUser;
//    private VacancyResponse testVacancyResponse;
//    private List<VacancyResponse> testVacancyResponses;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//
//        // Create test user
//        testUser = new User();
//        testUser.setId(1L);
//        testUser.setName("Test User");
//        testUser.setEmail("test@example.com");
//        testUser.setPassword("password");
//
//        // Create test vacancy response
//        testVacancyResponse = new VacancyResponse();
//        testVacancyResponse.setId(1L);
//        testVacancyResponse.setUser(testUser);
//        testVacancyResponse.setCompanyName("Test Company");
//        testVacancyResponse.setPosition("Software Engineer");
//        testVacancyResponse.setTechnologies("Java, Spring Boot");
//        testVacancyResponse.setPros("Good salary, remote work");
//        testVacancyResponse.setCons("Long hours");
//        testVacancyResponse.setNotes("Applied through company website");
//        testVacancyResponse.setSalary("$100,000");
//        testVacancyResponse.setLocation("New York");
//        testVacancyResponse.setInterviewStage(InterviewStage.APPLIED);
//        testVacancyResponse.setUpdatedAt(LocalDateTime.now());
//        testVacancyResponse.setTags(new HashSet<>());
//
//        testVacancyResponses = new ArrayList<>();
//        testVacancyResponses.add(testVacancyResponse);
//    }
//
//    @Test
//    public void testSave() {
//        when(vacancyResponseRepository.save(any(VacancyResponse.class))).thenReturn(testVacancyResponse);
//
//        VacancyResponse savedVacancyResponse = vacancyResponseService.save(testVacancyResponse);
//
//        assertEquals(testVacancyResponse, savedVacancyResponse);
//        verify(vacancyResponseRepository, times(1)).save(testVacancyResponse);
//    }
//
//    @Test
//    public void testFindAll() {
//        when(vacancyResponseRepository.findAll()).thenReturn(testVacancyResponses);
//
//        List<VacancyResponse> foundVacancyResponses = vacancyResponseService.findAll();
//
//        assertEquals(testVacancyResponses, foundVacancyResponses);
//        verify(vacancyResponseRepository, times(1)).findAll();
//    }
//
//    @Test
//    public void testFindById() {
//        when(vacancyResponseRepository.findById(1L)).thenReturn(Optional.of(testVacancyResponse));
//
//        Optional<VacancyResponse> foundVacancyResponse = vacancyResponseService.findById(1L);
//
//        assertTrue(foundVacancyResponse.isPresent());
//        assertEquals(testVacancyResponse, foundVacancyResponse.get());
//        verify(vacancyResponseRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    public void testDeleteById() {
//        doNothing().when(vacancyResponseRepository).deleteById(1L);
//
//        vacancyResponseService.deleteById(1L);
//
//        verify(vacancyResponseRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    public void testGetVacancyResponsesByUser() {
//        when(vacancyResponseRepository.findByUser(testUser)).thenReturn(testVacancyResponses);
//
//        List<VacancyResponse> foundVacancyResponses = vacancyResponseService.getVacancyResponsesByUser(testUser);
//
//        assertEquals(testVacancyResponses, foundVacancyResponses);
//        verify(vacancyResponseRepository, times(1)).findByUser(testUser);
//    }
//
//    @Test
//    public void testGetVacancyResponsesByUserId() {
//        when(vacancyResponseRepository.findByUserId(1L)).thenReturn(testVacancyResponses);
//
//        List<VacancyResponse> foundVacancyResponses = vacancyResponseService.getVacancyResponsesByUserId(1L);
//
//        assertEquals(testVacancyResponses, foundVacancyResponses);
//        verify(vacancyResponseRepository, times(1)).findByUserId(1L);
//    }
//
//    @Test
//    public void testGetVacancyResponsesByCompanyName() {
//        when(vacancyResponseRepository.findByCompanyNameContainingIgnoreCase("Test")).thenReturn(testVacancyResponses);
//
//        List<VacancyResponse> foundVacancyResponses = vacancyResponseService.getVacancyResponsesByCompanyName("Test");
//
//        assertEquals(testVacancyResponses, foundVacancyResponses);
//        verify(vacancyResponseRepository, times(1)).findByCompanyNameContainingIgnoreCase("Test");
//    }
//
//    @Test
//    public void testGetVacancyResponsesByPosition() {
//        when(vacancyResponseRepository.findByPositionContainingIgnoreCase("Engineer")).thenReturn(testVacancyResponses);
//
//        List<VacancyResponse> foundVacancyResponses = vacancyResponseService.getVacancyResponsesByPosition("Engineer");
//
//        assertEquals(testVacancyResponses, foundVacancyResponses);
//        verify(vacancyResponseRepository, times(1)).findByPositionContainingIgnoreCase("Engineer");
//    }
//}