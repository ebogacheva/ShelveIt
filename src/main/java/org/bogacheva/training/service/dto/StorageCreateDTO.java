package org.bogacheva.training.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bogacheva.training.domain.storage.StorageType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageCreateDTO {
    private Long id;
    private String name;
    private StorageType type;
    private Long parentId;
}
