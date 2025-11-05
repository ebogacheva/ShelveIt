package org.bogacheva.training.service.command;

import lombok.Getter;
import lombok.ToString;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.service.dto.StorageCreateDTO;

@Getter
@ToString
public final class CreateStorageCommand extends BaseCommand {
    private final String name;
    private final StorageType type;
    private final Long parentId;

    public CreateStorageCommand(String name, StorageType type, Long parentId) {
        super(CommandType.CREATE_STORAGE);
        this.name = name;
        this.type = type;
        this.parentId = parentId;
    }

    public StorageCreateDTO getStorageCreateDTO() {
        return new StorageCreateDTO(this.name, this.type, this.parentId);
    }
}
