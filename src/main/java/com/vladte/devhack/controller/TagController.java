package com.vladte.devhack.controller;

import com.vladte.devhack.dto.TagDTO;
import com.vladte.devhack.mapper.TagMapper;
import com.vladte.devhack.model.Tag;
import com.vladte.devhack.model.User;
import com.vladte.devhack.service.api.QuestionGenerationOrchestrationService;
import com.vladte.devhack.service.domain.TagService;
import com.vladte.devhack.service.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for handling requests related to tags.
 */
@Controller
@RequestMapping("/tags")
public class TagController extends BaseCrudController<Tag, TagDTO, UUID, TagService, TagMapper> {

    private final TagMapper mapper;

    private final UserService userService;
    private final QuestionGenerationOrchestrationService questionGenerationOrchestrationService;

    @Autowired
    public TagController(TagService tagService, TagMapper tagMapper, UserService userService, QuestionGenerationOrchestrationService questionGenerationOrchestrationService) {
        super(tagService, tagMapper);
        this.mapper = tagMapper;
        this.userService = userService;
        this.questionGenerationOrchestrationService = questionGenerationOrchestrationService;
    }

    @Override
    protected String getListViewName() {
        return "tags/list";
    }

    @Override
    protected String getDetailViewName() {
        return "tags/view";
    }

    @Override
    protected String getListPageTitle() {
        return "Tags";
    }

    @Override
    protected String getDetailPageTitle() {
        return "Tag Details";
    }

    @Override
    protected String getEntityName() {
        return "Tag";
    }

    /**
     * Display a list of all tags.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @Override
    public String list(Model model) {
        List<Tag> tags = service.findAll();

        // Get the first user for progress calculation (default user)
        Optional<User> userOpt = userService.findAll().stream().findFirst();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Calculate progress for all tags
            tags = ((TagService) service).calculateProgressForAll(tags, user);
        }

        // Convert entities to DTOs
        List<TagDTO> tagDTOs = mapper.toDTOList(tags);
        model.addAttribute("tags", tagDTOs);
        setPageTitle(model, "Tags");
        return "tags/list";
    }

    /**
     * Display the form for creating a new tag.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/new")
    public String newTagForm(Model model) {
        model.addAttribute("tag", new TagDTO());
        model.addAttribute("pageTitle", "Create New Tag");
        return "tags/form";
    }

    /**
     * Display the form for editing an existing tag.
     *
     * @param id    the ID of the tag to edit
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/{id}/edit")
    public String editTagForm(@PathVariable UUID id, Model model) {
        Tag tag = getEntityOrThrow(service.findById(id), "Tag not found");
        // Convert entity to DTO
        TagDTO tagDTO = mapper.toDTO(tag);
        model.addAttribute("tag", tagDTO);
        setPageTitle(model, "Edit Tag");
        return "tags/form";
    }

    /**
     * Process the form submission for creating or updating a tag.
     *
     * @param tagDTO the tag data from the form
     * @return a redirect to the tag list
     */
    @PostMapping
    public String saveTag(@ModelAttribute TagDTO tagDTO) {
        // Convert DTO to entity
        Tag tag = mapper.toEntity(tagDTO);
        service.save(tag);
        // Start the asynchronous generation process without blocking
        questionGenerationOrchestrationService.startEasyQuestionGeneration(tag.getName());
        return "redirect:/tags";
    }

    /**
     * Delete a tag.
     *
     * @param id the ID of the tag to delete
     * @return a redirect to the tag list
     */
    @GetMapping("/{id}/delete")
    public String deleteTag(@PathVariable UUID id) {
        service.deleteById(id);
        return "redirect:/tags";
    }

    /**
     * Search for tags by name.
     *
     * @param name  the name to search for
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/search")
    public String searchTags(@RequestParam String name, Model model) {
        Optional<Tag> tagOpt = service.findTagByName(name);

        if (tagOpt.isPresent()) {
            Tag tag = tagOpt.get();

            // Get the first user for progress calculation (default user)
            Optional<User> userOpt = userService.findAll().stream().findFirst();
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // Calculate progress for the tag
                tag = service.calculateProgress(tag, user);
            }

            // Convert entity to DTO
            TagDTO tagDTO = mapper.toDTO(tag);
            model.addAttribute("tags", List.of(tagDTO));
        } else {
            model.addAttribute("tags", List.of());
            model.addAttribute("message", "No tags found with name: " + name);
        }

        setPageTitle(model, "Search Results for: " + name);
        return "tags/list";
    }

    /**
     * View a tag.
     *
     * @param id    the tag ID
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @Override
    public String view(@PathVariable UUID id, Model model) {
        Tag tag = getEntityOrThrow(service.findById(id), "Tag not found");

        Optional<User> userOpt = userService.findAll().stream().findFirst();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            tag = service.calculateProgress(tag, user);
        }

        model.addAttribute("tag", tag);
        setPageTitle(model, "Tag Details");
        return "tags/view";
    }
}
