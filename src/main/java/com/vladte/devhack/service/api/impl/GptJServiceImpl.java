package com.vladte.devhack.service.api.impl;

import com.vladte.devhack.service.api.AbstractAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Implementation of the OpenAiService interface for interacting with the GPT-J API via LocalAI.
 */
@Profile("local")
@Service("gptJService")
public class GptJServiceImpl extends AbstractAiService {

    @Value("${gptj.api.key}")
    private String apiKey;

    @Value("${gptj.model}")
    private String model;

    @Value("${gptj.max-tokens}")
    private int maxTokens;

    @Value("${gptj.api.url}")
    private String apiUrl;

    /**
     * Default constructor.
     */
    public GptJServiceImpl() {
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
        return apiUrl;
    }
}
