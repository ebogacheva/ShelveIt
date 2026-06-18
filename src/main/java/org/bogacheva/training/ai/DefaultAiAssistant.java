package org.bogacheva.training.ai;

import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.item.search.ItemSearchService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultAiAssistant implements AiAssistant {

    private static final int FIND_LIMIT = 5;

    private final ChatClient chatClient;
    private final EmbeddingService embeddingService;
    private final ItemSearchService itemSearchService;
    private final String systemPrompt;

    public DefaultAiAssistant(@Qualifier("anthropicChatClient") ChatClient chatClient,
                               EmbeddingService embeddingService,
                               ItemSearchService itemSearchService,
                               @Qualifier("interpretPutSystemPrompt") String systemPrompt) {
        this.chatClient = chatClient;
        this.embeddingService = embeddingService;
        this.itemSearchService = itemSearchService;
        this.systemPrompt = systemPrompt;
    }

    @Override
    public List<ItemDTO> findItems(String query) {
        float[] vector = embeddingService.embed(query);
        String vectorString = embeddingService.toVectorString(vector);
        return itemSearchService.findNearestItems(vectorString, FIND_LIMIT);
    }

    @Override
    public PutInterpretation interpretPut(String userInput) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userInput)
                .call()
                .entity(PutInterpretation.class);
    }
}
