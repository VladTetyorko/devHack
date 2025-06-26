package com.vladte.devhack.service;

import com.vladte.devhack.model.Tag;
import com.vladte.devhack.repository.TagRepository;
import com.vladte.devhack.service.domain.impl.TagServiceImpl;
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

public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag testTag;
    private List<Tag> testTags;
    private final UUID TEST_TAG_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test data
        testTag = new Tag();
        testTag.setId(TEST_TAG_ID);
        testTag.setName("Test Tag");
        testTag.setQuestions(new HashSet<>());

        testTags = new ArrayList<>();
        testTags.add(testTag);
    }

    @Test
    public void testSave() {
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        Tag savedTag = tagService.save(testTag);

        assertEquals(testTag, savedTag);
        verify(tagRepository, times(1)).save(testTag);
    }

    @Test
    public void testFindAll() {
        when(tagRepository.findAll()).thenReturn(testTags);

        List<Tag> foundTags = tagService.findAll();

        assertEquals(testTags, foundTags);
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(tagRepository.findById(TEST_TAG_ID)).thenReturn(Optional.of(testTag));

        Optional<Tag> foundTag = tagService.findById(TEST_TAG_ID);

        assertTrue(foundTag.isPresent());
        assertEquals(testTag, foundTag.get());
        verify(tagRepository, times(1)).findById(TEST_TAG_ID);
    }

    @Test
    public void testDeleteById() {
        doNothing().when(tagRepository).deleteById(TEST_TAG_ID);

        tagService.deleteById(TEST_TAG_ID);

        verify(tagRepository, times(1)).deleteById(TEST_TAG_ID);
    }

    @Test
    public void testFindTagByName() {
        when(tagRepository.findByName("Test Tag")).thenReturn(Optional.of(testTag));

        Optional<Tag> foundTag = tagService.findTagByName("Test Tag");

        assertTrue(foundTag.isPresent());
        assertEquals(testTag, foundTag.get());
        verify(tagRepository, times(1)).findByName("Test Tag");
    }

    @Test
    public void testFindTagByNameNotFound() {
        when(tagRepository.findByName("Nonexistent Tag")).thenReturn(Optional.empty());

        Optional<Tag> foundTag = tagService.findTagByName("Nonexistent Tag");

        assertTrue(foundTag.isEmpty());
        verify(tagRepository, times(1)).findByName("Nonexistent Tag");
    }
}
