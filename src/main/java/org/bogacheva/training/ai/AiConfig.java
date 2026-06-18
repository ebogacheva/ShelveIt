package org.bogacheva.training.ai;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
class AiConfig {

    @Bean
    ChatClient anthropicChatClient(AnthropicChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    String interpretPutSystemPrompt(
            @Value("classpath:prompts/interpret-put-system.txt") Resource resource
    ) throws IOException {
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }
}
