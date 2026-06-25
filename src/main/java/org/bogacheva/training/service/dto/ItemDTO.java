package org.bogacheva.training.service.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private Long id;
    private String name;
    private StorageDTO storage;
    private List<String> keywords;
    private Map<String, Object> attributes;

    @Override
    public String toString() {
        return String.format(
                "%-8s %-20s %-18s %-20s %-20s",
                "id: " + id,
                "name: " + name,
                "storage: " + (storage != null ? storage.getId() : "-"),
                "keywords: " + (keywords != null ? String.join(", ", keywords) : "-"),
                "attributes: " + (attributes != null ? attributes : "-")
        );
    }
}
