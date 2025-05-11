package org.bogacheva.training.view.commands;

import lombok.Getter;
import org.bogacheva.training.service.dto.ItemCreateDTO;


public final class CreateItemCommand extends BaseCommand {
    private final String name;
    private final Long storageId;

    public CreateItemCommand(String name, Long storageId) {
        super(CommandType.CREATE_ITEM);
        this.name = name;
        this.storageId = storageId;
    }

    public ItemCreateDTO getCreateItemDTO() {
        return new ItemCreateDTO(this.name, this.storageId);
    }
}