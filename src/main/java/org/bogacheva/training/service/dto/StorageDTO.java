package org.bogacheva.training.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bogacheva.training.domain.storage.StorageType;

import java.util.List;

@Getter
@Setter
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
        return "ID: " + id + " : " + name + " " + type + " parent: " + parentId;
    }
}
