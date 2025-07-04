package com.vladte.devhack.service.api.impl;

import com.vladte.devhack.service.api.AbstractAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implementation of the OpenAiService interface for interacting with the OpenAI API.
 */
@Service("openApiService")
public class OpenAiServiceImpl extends AbstractAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.max-tokens}")
    private int maxTokens;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    /**
     * Default constructor.
     */
    public OpenAiServiceImpl() {
        super();
    }

    @Override
    protected String getApiKey() {
        return apiKey;
    }

    @Override
    protected String getModel() {
        return model;
    }

    @Override
    protected int getMaxTokens() {
        return maxTokens;
    }

    @Override
    protected String getApiUrl() {
        return OPENAI_API_URL;
    }
}
