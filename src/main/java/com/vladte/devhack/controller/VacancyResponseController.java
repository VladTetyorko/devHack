package com.vladte.devhack.controller;

import com.vladte.devhack.model.VacancyResponse;
import com.vladte.devhack.model.User;
import com.vladte.devhack.model.InterviewStage;
import com.vladte.devhack.service.domain.VacancyResponseService;
import com.vladte.devhack.service.domain.UserService;
import com.vladte.devhack.service.view.VacancyResponseViewService;
import com.vladte.devhack.service.view.VacancyResponseFormService;
import com.vladte.devhack.service.view.VacancyResponseDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for handling requests related to vacancy responses.
 */
@Controller
@RequestMapping("/vacancies")
public class VacancyResponseController extends BaseCrudController<VacancyResponse, UUID, VacancyResponseService> {

    private final UserService userService;
    private final VacancyResponseViewService vacancyResponseViewService;
    private final VacancyResponseFormService vacancyResponseFormService;
    private final VacancyResponseDashboardService vacancyResponseDashboardService;

    @Autowired
    public VacancyResponseController(
            VacancyResponseService vacancyResponseService, 
            UserService userService,
            VacancyResponseViewService vacancyResponseViewService,
            VacancyResponseFormService vacancyResponseFormService,
            VacancyResponseDashboardService vacancyResponseDashboardService) {
        super(vacancyResponseService);
        this.userService = userService;
        this.vacancyResponseViewService = vacancyResponseViewService;
        this.vacancyResponseFormService = vacancyResponseFormService;
        this.vacancyResponseDashboardService = vacancyResponseDashboardService;
    }

    @Override
    protected String getListViewName() {
        return "vacancies/list";
    }

    @Override
    protected String getDetailViewName() {
        return "vacancies/view";
    }

    @Override
    protected String getListPageTitle() {
        return "Vacancy Responses";
    }

    @Override
    protected String getDetailPageTitle() {
        return "View Vacancy Response";
    }

    @Override
    protected String getEntityName() {
        return "VacancyResponse";
    }

    /**
     * Display the work dashboard with application tracking information.
     *
     * @param page the page number for the "Top Companies" section
     * @param size the page size for the "Top Companies" section
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        // Delegate to the dashboard service with pagination
        vacancyResponseDashboardService.prepareDashboardModel(page, size, model);
        vacancyResponseDashboardService.setDashboardPageTitle(model);
        return "vacancies/main";
    }

    @Override
    public String list(Model model) {
        // Delegate to the search method with default parameters
        return search(null, null, 0, 10, model);
    }

    /**
     * Search and filter vacancy responses with pagination.
     *
     * @param query the search query
     * @param stage the interview stage to filter by
     * @param page the page number
     * @param size the page size
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String stage,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Delegate to the view service
        vacancyResponseViewService.prepareSearchResultsModel(query, stage, page, size, model);
        vacancyResponseViewService.setSearchResultsPageTitle(model);
        return "vacancies/list";
    }

    /**
     * Display a form for creating a new vacancy response.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/new")
    public String newVacancyResponseForm(Model model) {
        // Delegate to the form service
        vacancyResponseFormService.prepareNewVacancyResponseForm(model);
        vacancyResponseFormService.setNewVacancyResponsePageTitle(model);
        return "vacancies/form";
    }

    /**
     * Display a form for editing an existing vacancy response.
     *
     * @param id the ID of the vacancy response to edit
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/{id}/edit")
    public String editVacancyResponseForm(@PathVariable UUID id, Model model) {
        // Delegate to the form service
        VacancyResponse vacancyResponse = vacancyResponseFormService.prepareEditVacancyResponseForm(id, model);
        if (vacancyResponse == null) {
            throw new IllegalArgumentException("Vacancy response not found");
        }
        vacancyResponseFormService.setEditVacancyResponsePageTitle(model);
        return "vacancies/form";
    }

    /**
     * Save a vacancy response.
     *
     * @param vacancyResponse the vacancy response to save
     * @param userId the ID of the user to associate with the vacancy response
     * @return a redirect to the vacancy response list
     */
    @PostMapping
    public String saveVacancyResponse(@ModelAttribute VacancyResponse vacancyResponse, @RequestParam UUID userId) {
        // Delegate to the form service
        VacancyResponse savedResponse = vacancyResponseFormService.saveVacancyResponse(vacancyResponse, userId);
        if (savedResponse == null) {
            throw new IllegalArgumentException("User not found");
        }
        return "redirect:/vacancies";
    }

    /**
     * Delete a vacancy response.
     *
     * @param id the ID of the vacancy response to delete
     * @return a redirect to the vacancy response list
     */
    @GetMapping("/{id}/delete")
    public String deleteVacancyResponse(@PathVariable UUID id) {
        // Delegate to the form service
        vacancyResponseFormService.deleteVacancyResponse(id);
        return "redirect:/vacancies";
    }

    /**
     * Display a list of vacancy responses for a specific user.
     *
     * @param userId the ID of the user to find vacancy responses for
     * @param page the page number
     * @param size the page size
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/user/{userId}")
    public String getVacancyResponsesByUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        User user = getEntityOrThrow(userService.findById(userId), "User not found");

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Get user's vacancies with pagination
        Page<VacancyResponse> vacancyResponsePage = service.getVacancyResponsesByUser(user, pageable);

        // Add pagination data to model
        model.addAttribute("vacancyResponses", vacancyResponsePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vacancyResponsePage.getTotalPages());
        model.addAttribute("totalItems", vacancyResponsePage.getTotalElements());
        model.addAttribute("size", size);

        model.addAttribute("user", user);
        setPageTitle(model, "Vacancy Responses for " + user.getName());
        return "vacancies/list";
    }
}
