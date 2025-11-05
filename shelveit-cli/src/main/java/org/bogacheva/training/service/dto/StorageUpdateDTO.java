package org.bogacheva.training.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.bogacheva.training.domain.storage.StorageType;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageUpdateDTO {

    private String name;

    private StorageType type;
}
