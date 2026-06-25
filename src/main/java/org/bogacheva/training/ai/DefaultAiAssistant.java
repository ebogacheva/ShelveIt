package org.bogacheva.training.ai;

import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.item.search.ItemSearchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultAiAssistant implements AiAssistant {

    private static final int FIND_LIMIT = 5;

    private final EmbeddingService embeddingService;
    private final ItemSearchService itemSearchService;

    public DefaultAiAssistant(EmbeddingService embeddingService,
                               ItemSearchService itemSearchService) {
        this.embeddingService = embeddingService;
        this.itemSearchService = itemSearchService;
    }

    @Override
    public List<ItemDTO> findItems(String query) {
        float[] vector = embeddingService.embed(query);
        String vectorString = embeddingService.toVectorString(vector);
        return itemSearchService.findNearestItems(vectorString, FIND_LIMIT);
    }
}
