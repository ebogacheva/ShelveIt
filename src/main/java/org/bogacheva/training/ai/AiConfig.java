package org.bogacheva.training.ai;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AiConfig {

    @Bean
    ChatClient anthropicChatClient(AnthropicChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
