package org.bogacheva.training.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateDTO {

    @NotBlank(message = "Item name cannot be empty")
    private String name;

    @NotBlank(message = "Any storage should be specified")
    private Long storageId;

    private List<String> keywords;
}
