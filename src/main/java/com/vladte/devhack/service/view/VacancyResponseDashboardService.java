package com.vladte.devhack.service.view;

import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

/**
 * Service interface for preparing the vacancy response dashboard view.
 * This interface follows the Single Responsibility Principle by focusing only on dashboard view preparation.
 */
public interface VacancyResponseDashboardService {

    /**
     * Prepare the model for the dashboard view.
     *
     * @param model the model to add attributes to
     */
    void prepareDashboardModel(Model model);

    /**
     * Prepare the model for the dashboard view with pagination for the vacancy responses.
     *
     * @param page the page number
     * @param size the page size
     * @param model the model to add attributes to
     * @return the pageable object created for the dashboard
     */
    Pageable prepareDashboardModel(int page, int size, Model model);

    /**
     * Set the page title for the dashboard page.
     *
     * @param model the model to add the title to
     */
    void setDashboardPageTitle(Model model);
}
