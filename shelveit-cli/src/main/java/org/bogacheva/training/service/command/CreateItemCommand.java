package org.bogacheva.training.service.command;

import lombok.Getter;
import lombok.ToString;
import org.bogacheva.training.service.dto.ItemCreateDTO;

import java.util.List;

@Getter
@ToString
public final class CreateItemCommand extends BaseCommand {
    private final String name;
    private final Long storageId;
    private final List<String> keywords;

    public CreateItemCommand(String name, Long storageId, List<String> keywords) {
        super(CommandType.CREATE_ITEM);
        this.name = name;
        this.storageId = storageId;
        this.keywords = keywords;
    }

    public ItemCreateDTO getCreateItemDTO() {
        return new ItemCreateDTO(this.name, this.storageId, this.keywords);
    }
}