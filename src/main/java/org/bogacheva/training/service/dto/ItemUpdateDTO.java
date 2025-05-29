package org.bogacheva.training.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemUpdateDTO {
    private String name;
    private List<String> keywords;
    private Long storageId;
}
