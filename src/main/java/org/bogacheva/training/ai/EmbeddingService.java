package org.bogacheva.training.ai;

public interface EmbeddingService {
    float[] embed(String text);
    String toVectorString(float[] embedding);
}
