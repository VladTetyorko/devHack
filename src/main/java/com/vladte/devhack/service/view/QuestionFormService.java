package com.vladte.devhack.service.view;

import com.vladte.devhack.model.InterviewQuestion;
import org.springframework.ui.Model;

import java.util.UUID;

/**
 * Service interface for handling question form operations.
 * This interface follows the Single Responsibility Principle by focusing only on form-related operations.
 */
public interface QuestionFormService {

    /**
     * Prepare the model for creating a new question.
     *
     * @param model the model to add attributes to
     */
    void prepareNewQuestionForm(Model model);

    /**
     * Prepare the model for editing an existing question.
     *
     * @param id the ID of the question to edit
     * @param model the model to add attributes to
     * @return the question being edited, or null if not found
     */
    InterviewQuestion prepareEditQuestionForm(UUID id, Model model);

    /**
     * Save a question from form submission.
     *
     * @param question the question data from the form
     * @return the saved question
     */
    InterviewQuestion saveQuestion(InterviewQuestion question);

    /**
     * Delete a question by ID.
     *
     * @param id the ID of the question to delete
     */
    void deleteQuestion(UUID id);

    /**
     * Set the page title for the new question form.
     *
     * @param model the model to add the title to
     */
    void setNewQuestionPageTitle(Model model);

    /**
     * Set the page title for the edit question form.
     *
     * @param model the model to add the title to
     */
    void setEditQuestionPageTitle(Model model);

    /**
     * Prepare the model for generating questions using AI.
     *
     * @param model the model to add attributes to
     */
    void prepareGenerateQuestionsForm(Model model);

    /**
     * Set the page title for the generate questions form.
     *
     * @param model the model to add the title to
     */
    void setGenerateQuestionsPageTitle(Model model);

    /**
     * Prepare the model for auto-generating easy questions using AI.
     *
     * @param model the model to add attributes to
     */
    void prepareAutoGenerateQuestionsForm(Model model);

    /**
     * Set the page title for the auto-generate questions form.
     *
     * @param model the model to add the title to
     */
    void setAutoGenerateQuestionsPageTitle(Model model);
}
