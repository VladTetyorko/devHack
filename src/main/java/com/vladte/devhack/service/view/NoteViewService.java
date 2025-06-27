package com.vladte.devhack.service.view;

import com.vladte.devhack.dto.NoteDTO;
import com.vladte.devhack.model.User;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for preparing the note view.
 * This interface follows the Single Responsibility Principle by focusing only on view preparation.
 */
public interface NoteViewService {

    /**
     * Prepare the model for the list view of notes for the current user.
     *
     * @param model the model to add attributes to
     * @return the list of note DTOs
     */
    List<NoteDTO> prepareCurrentUserNotesModel(Model model);

    /**
     * Set the page title for the current user's notes page.
     *
     * @param model the model to add the title to
     */
    void setCurrentUserNotesPageTitle(Model model);

    /**
     * Prepare the model for the user-specific notes view.
     *
     * @param userId the ID of the user to find notes for
     * @param model  the model to add attributes to
     * @return the user whose notes are being displayed
     */
    User prepareUserNotesModel(UUID userId, Model model);

    /**
     * Set the page title for the user-specific notes page.
     *
     * @param model the model to add the title to
     * @param user  the user whose notes are being displayed
     */
    void setUserNotesPageTitle(Model model, User user);

    /**
     * Prepare the model for the question-specific notes view.
     *
     * @param questionId the ID of the question to find notes for
     * @param model      the model to add attributes to
     * @return the list of note DTOs
     */
    List<NoteDTO> prepareQuestionNotesModel(UUID questionId, Model model);

    /**
     * Set the page title for the question-specific notes page.
     *
     * @param model        the model to add the title to
     * @param questionText the text of the question whose notes are being displayed
     */
    void setQuestionNotesPageTitle(Model model, String questionText);

    /**
     * Prepare the model for the note detail view.
     *
     * @param id    the ID of the note to display
     * @param model the model to add attributes to
     * @return the note DTO
     */
    NoteDTO prepareNoteDetailModel(UUID id, Model model);

    /**
     * Set the page title for the note detail page.
     *
     * @param model the model to add the title to
     */
    void setNoteDetailPageTitle(Model model);
}