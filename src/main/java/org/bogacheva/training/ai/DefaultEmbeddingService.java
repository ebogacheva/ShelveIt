package org.bogacheva.training.ai;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;

@Service
public class DefaultEmbeddingService implements EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public DefaultEmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public float[] embed(String text) {
        return embeddingModel.embed(text);
    }

    @Override
    public String toVectorString(float[] embedding) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (float v : embedding) {
            joiner.add(String.valueOf(v));
        }
        return joiner.toString();
    }
}
