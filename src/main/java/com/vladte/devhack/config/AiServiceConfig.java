package com.vladte.devhack.config;

import com.vladte.devhack.service.api.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for AI services.
 * This class provides a way to select which AI service to use (OpenAI or GPT-J)
 * based on a configuration property.
 */
@Configuration
public class AiServiceConfig {

    @Value("${ai.service.provider:openai}")
    private String aiServiceProvider;

    /**
     * Provides the primary AI service based on the configured provider.
     *
     * @param openAiService The OpenAI service implementation
     * @param gptJService   The GPT-J service implementation
     * @return The selected AI service implementation
     */
    private final OpenAiService openAiService;
    private OpenAiService gptJService;

    @Autowired
    public AiServiceConfig(@Qualifier("openApiService") OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Autowired(required = false)
    public void setGptJService(@Qualifier("gptJService") OpenAiService gptJService) {
        this.gptJService = gptJService;
    }

    @Bean
    @Primary
    public OpenAiService aiService() {
        if ("openai".equals(aiServiceProvider) || gptJService == null) {
            return openAiService;
        } else {
            return gptJService;
        }
    }
}
