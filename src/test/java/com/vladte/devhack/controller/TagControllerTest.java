//package com.vladte.devhack.controller;
//
//import com.vladte.devhack.model.Tag;
//import com.vladte.devhack.service.domain.TagService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.ui.Model;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//public class TagControllerTest {
//
//    @Mock
//    private TagService tagService;
//
//    @Mock
//    private Model model;
//
//    @InjectMocks
//    private TagController tagController;
//
//    private MockMvc mockMvc;
//
//    private Tag testTag;
//    private List<Tag> testTags;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
//
//        // Create test data
//        testTag = new Tag();
//        testTag.setId(1L);
//        testTag.setName("Test Tag");
//        testTag.setQuestions(new HashSet<>());
//
//        testTags = new ArrayList<>();
//        testTags.add(testTag);
//    }
//
//    @Test
//    public void testList() throws Exception {
//        when(tagService.findAll()).thenReturn(testTags);
//
//        mockMvc.perform(get("/tags"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("tags/list"))
//                .andExpect(model().attributeExists("tags"))
//                .andExpect(model().attribute("tags", testTags));
//
//        verify(tagService, times(1)).findAll();
//    }
//
//    @Test
//    public void testViewTag() throws Exception {
//        when(tagService.findById(1L)).thenReturn(Optional.of(testTag));
//
//        mockMvc.perform(get("/tags/1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("tags/view"))
//                .andExpect(model().attributeExists("tag"))
//                .andExpect(model().attribute("tag", testTag));
//
//        verify(tagService, times(1)).findById(1L);
//    }
//
//    @Test
//    public void testNewTagForm() throws Exception {
//        mockMvc.perform(get("/tags/new"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("tags/form"))
//                .andExpect(model().attributeExists("tag"));
//    }
//
//    @Test
//    public void testEditTagForm() throws Exception {
//        when(tagService.findById(1L)).thenReturn(Optional.of(testTag));
//
//        mockMvc.perform(get("/tags/1/edit"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("tags/form"))
//                .andExpect(model().attributeExists("tag"))
//                .andExpect(model().attribute("tag", testTag));
//
//        verify(tagService, times(1)).findById(1L);
//    }
//
//    @Test
//    public void testSaveTag() throws Exception {
//        when(tagService.save(any(Tag.class))).thenReturn(testTag);
//
//        mockMvc.perform(post("/tags")
//                .param("name", "Test Tag"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/tags"));
//
//        verify(tagService, times(1)).save(any(Tag.class));
//    }
//
//    @Test
//    public void testDeleteTag() throws Exception {
//        mockMvc.perform(get("/tags/1/delete"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/tags"));
//
//        verify(tagService, times(1)).deleteById(1L);
//    }
//
//    @Test
//    public void testSearchTagsFound() throws Exception {
//        when(tagService.findTagByName("Test Tag")).thenReturn(Optional.of(testTag));
//
//        mockMvc.perform(get("/tags/search").param("name", "Test Tag"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("tags/list"))
//                .andExpect(model().attributeExists("tags"))
//                .andExpect(model().attribute("tags", List.of(testTag)));
//
//        verify(tagService, times(1)).findTagByName("Test Tag");
//    }
//
//    @Test
//    public void testSearchTagsNotFound() throws Exception {
//        when(tagService.findTagByName("Nonexistent Tag")).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/tags/search").param("name", "Nonexistent Tag"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("tags/list"))
//                .andExpect(model().attributeExists("tags"))
//                .andExpect(model().attributeExists("message"))
//                .andExpect(model().attribute("tags", List.of()));
//
//        verify(tagService, times(1)).findTagByName("Nonexistent Tag");
//    }
//}
