package org.bogacheva.training.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.service.validation.ValidStorageType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageCreateDTO {

    @NotBlank(message = "Storage name should not be empty")
    private String name;

    @NotNull
    @ValidStorageType(enumClass = StorageType.class)
    private StorageType type;

    private Long parentId;
}
