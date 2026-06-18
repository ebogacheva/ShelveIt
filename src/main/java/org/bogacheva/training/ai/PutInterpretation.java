package org.bogacheva.training.ai;

import java.util.List;
import java.util.Map;

public record PutInterpretation(
        String name,
        String storageHint,
        Map<String, Object> attributes
) {
    public List<String> deriveKeywords() {
        if (attributes == null || attributes.isEmpty()) return List.of();
        return attributes.values().stream()
                .map(v -> v.toString().toLowerCase())
                .toList();
    }
}
