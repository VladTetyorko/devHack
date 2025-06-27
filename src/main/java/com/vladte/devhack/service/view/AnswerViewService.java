package com.vladte.devhack.service.view;

import com.vladte.devhack.dto.AnswerDTO;
import com.vladte.devhack.model.User;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for preparing the answer view.
 * This interface follows the Single Responsibility Principle by focusing only on view preparation.
 */
public interface AnswerViewService {

    /**
     * Prepare the model for the list view of answers for the current user.
     *
     * @param model the model to add attributes to
     * @return the list of answer DTOs
     */
    List<AnswerDTO> prepareCurrentUserAnswersModel(Model model);

    /**
     * Set the page title for the current user's answers page.
     *
     * @param model the model to add the title to
     */
    void setCurrentUserAnswersPageTitle(Model model);

    /**
     * Prepare the model for the user-specific answers view.
     *
     * @param userId the ID of the user to find answers for
     * @param model  the model to add attributes to
     * @return the user whose answers are being displayed
     */
    User prepareUserAnswersModel(UUID userId, Model model);

    /**
     * Set the page title for the user-specific answers page.
     *
     * @param model the model to add the title to
     * @param user  the user whose answers are being displayed
     */
    void setUserAnswersPageTitle(Model model, User user);

    /**
     * Prepare the model for the question-specific answers view.
     *
     * @param questionId the ID of the question to find answers for
     * @param model      the model to add attributes to
     * @return the list of answer DTOs
     */
    List<AnswerDTO> prepareQuestionAnswersModel(UUID questionId, Model model);

    /**
     * Set the page title for the question-specific answers page.
     *
     * @param model        the model to add the title to
     * @param questionText the text of the question whose answers are being displayed
     */
    void setQuestionAnswersPageTitle(Model model, String questionText);

    /**
     * Prepare the model for the answer detail view.
     *
     * @param id    the ID of the answer to display
     * @param model the model to add attributes to
     * @return the answer DTO
     */
    AnswerDTO prepareAnswerDetailModel(UUID id, Model model);

    /**
     * Set the page title for the answer detail page.
     *
     * @param model the model to add the title to
     */
    void setAnswerDetailPageTitle(Model model);
}