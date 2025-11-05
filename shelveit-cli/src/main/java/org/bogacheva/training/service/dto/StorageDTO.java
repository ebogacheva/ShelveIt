package org.bogacheva.training.service.dto;

import lombok.*;
import org.bogacheva.training.domain.storage.StorageType;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageDTO {
    private Long id;
    private String name;
    private StorageType type;
    private List<Long> items;
    private List<Long> storages;
    private Long parentId;

    @Override
    public String toString() {
        return String.format(
                "%-8s %-20s %-20s %-10s",
                "id: " + id,
                "name: " + name,
                "type: " + type,
                "parent: " + (parentId != null ? parentId : "-")
        );
    }
}
