package org.bogacheva.training.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.service.validation.ValidStorageType;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageUpdateDTO {

    private String name;

    @ValidStorageType(enumClass = StorageType.class)
    private StorageType type;
}
