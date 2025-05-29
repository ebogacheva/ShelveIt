package org.bogacheva.training.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private Long id;
    private String name;
    private StorageDTO storage;
    private List<String> keywords;

    @Override
    public String toString() {
        return String.format(
                "%-8s %-20s %-18s %-20s",
                "id: " + id,
                "name: " + name,
                "storage: " + (storage != null ? storage.getId() : "-"),
                "keywords: " + (keywords != null ? String.join(", ", keywords) : "-")
        );
    }
}
