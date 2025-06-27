package com.vladte.devhack.service.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract base class for AI service implementations.
 * This class provides common functionality for different AI service implementations.
 */
public abstract class AbstractAiService implements OpenAiService {

    protected final RestTemplate restTemplate;

    /**
     * Default constructor that creates a RestTemplate instance.
     */
    protected AbstractAiService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Get the API key for the AI service.
     *
     * @return the API key
     */
    protected abstract String getApiKey();

    /**
     * Get the model name for the AI service.
     *
     * @return the model name
     */
    protected abstract String getModel();

    /**
     * Get the maximum number of tokens for the AI service.
     *
     * @return the maximum number of tokens
     */
    protected abstract int getMaxTokens();

    /**
     * Get the API URL for the AI service.
     *
     * @return the API URL
     */
    protected abstract String getApiUrl();

    /**
     * Generate text using the AI API.
     *
     * @param prompt the prompt to send to the API
     * @return the generated text
     */
    @Override
    public String generateText(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getApiKey());

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", getModel());
        requestBody.put("messages", List.of(message));
        requestBody.put("max_tokens", getMaxTokens());
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(getApiUrl(), request, Map.class);
            Map responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> messageResponse = (Map<String, Object>) choice.get("message");
                    return (String) messageResponse.get("content");
                }
            }
        } catch (Exception e) {
            return "Error generating text: " + e.getMessage();
        }

        return "Failed to generate text";
    }

    /**
     * Generate interview questions based on a tag.
     *
     * @param tag        the tag to generate questions for
     * @param count      the number of questions to generate
     * @param difficulty the difficulty level of the questions
     * @return the generated questions
     */
    @Override
    public String generateQuestionsForTag(String tag, int count, String difficulty) {
        String prompt = String.format(
                AiPromptConstraints.GENERATE_QUESTIONS_TEMPLATE,
                count, difficulty, tag);

        return generateText(prompt);
    }

    /**
     * Generate text using the AI API asynchronously.
     *
     * @param prompt the prompt to send to the API
     * @return a CompletableFuture containing the generated text
     */
    @Override
    @Async
    public CompletableFuture<String> generateTextAsync(String prompt) {
        String result = generateText(prompt);
        return CompletableFuture.completedFuture(result);
    }

    /**
     * Generate interview questions based on a tag asynchronously.
     *
     * @param tag        the tag to generate questions for
     * @param count      the number of questions to generate
     * @param difficulty the difficulty level of the questions
     * @return a CompletableFuture containing the generated questions
     */
    @Override
    @Async
    public CompletableFuture<String> generateQuestionsForTagAsync(String tag, int count, String difficulty) {
        String result = generateQuestionsForTag(tag, count, difficulty);
        return CompletableFuture.completedFuture(result);
    }

    /**
     * Check an answer to an interview question and provide a score.
     *
     * @param questionText the text of the interview question
     * @param answerText   the text of the answer to check
     * @return a score between 0 and 100 indicating how correct the answer is
     */
    @Override
    public Double checkAnswer(String questionText, String answerText) {
        String prompt = String.format(
                AiPromptConstraints.CHECK_ANSWER_TEMPLATE,
                questionText, answerText);

        String response = generateText(prompt);

        // Extract the numeric score from the response
        try {
            // Remove any non-numeric characters except for decimal points
            Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
            Matcher matcher = pattern.matcher(response);

            if (matcher.find()) {
                Double score = Double.parseDouble(matcher.group());
                // Ensure the score is within the valid range
                return Math.min(Math.max(score, 0.0), 100.0);
            }
        } catch (Exception e) {
            // If there's an error parsing the score, return a default value
            return 0.0;
        }

        // If no valid score was found, return a default value
        return 0.0;
    }

    /**
     * Check an answer to an interview question and provide a score asynchronously.
     *
     * @param questionText the text of the interview question
     * @param answerText   the text of the answer to check
     * @return a CompletableFuture containing a score between 0 and 100 indicating how correct the answer is
     */
    @Override
    @Async
    public CompletableFuture<Double> checkAnswerAsync(String questionText, String answerText) {
        Double result = checkAnswer(questionText, answerText);
        return CompletableFuture.completedFuture(result);
    }

    /**
     * Check an answer to an interview question and provide a score and feedback.
     *
     * @param questionText the text of the interview question
     * @param answerText   the text of the answer to check
     * @return a map containing the score and feedback
     */
    @Override
    public Map<String, Object> checkAnswerWithFeedback(String questionText, String answerText) {
        String prompt = String.format(
                AiPromptConstraints.CHECK_ANSWER_WITH_FEEDBACK_TEMPLATE,
                questionText, answerText);

        String response = generateText(prompt);
        Map<String, Object> result = new HashMap<>();

        // Extract the score and feedback from the response
        try {
            // Extract score
            Pattern scorePattern = Pattern.compile("Score:\\s*(\\d+(\\.\\d+)?)");
            Matcher scoreMatcher = scorePattern.matcher(response);

            if (scoreMatcher.find()) {
                Double score = Double.parseDouble(scoreMatcher.group(1));
                // Ensure the score is within the valid range
                result.put("score", Math.min(Math.max(score, 0.0), 100.0));
            } else {
                result.put("score", 0.0);
            }

            // Extract feedback
            Pattern feedbackPattern = Pattern.compile("Feedback:\\s*(.+)", Pattern.DOTALL);
            Matcher feedbackMatcher = feedbackPattern.matcher(response);

            if (feedbackMatcher.find()) {
                result.put("feedback", feedbackMatcher.group(1).trim());
            } else {
                // If no feedback section is found, use the entire response as feedback
                result.put("feedback", response.trim());
            }
        } catch (Exception e) {
            // If there's an error parsing the response, return default values
            result.put("score", 0.0);
            result.put("feedback", "Error evaluating answer: " + e.getMessage());
        }

        return result;
    }

    /**
     * Check an answer to an interview question and provide a score and feedback asynchronously.
     *
     * @param questionText the text of the interview question
     * @param answerText   the text of the answer to check
     * @return a CompletableFuture containing a map with the score and feedback
     */
    @Override
    @Async
    public CompletableFuture<Map<String, Object>> checkAnswerWithFeedbackAsync(String questionText, String answerText) {
        Map<String, Object> result = checkAnswerWithFeedback(questionText, answerText);
        return CompletableFuture.completedFuture(result);
    }
}
