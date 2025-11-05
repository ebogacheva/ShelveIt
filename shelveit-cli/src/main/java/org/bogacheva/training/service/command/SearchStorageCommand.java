package org.bogacheva.training.service.command;

import lombok.Getter;
import lombok.ToString;
import org.bogacheva.training.domain.storage.StorageType;

@Getter
@ToString
public final class SearchStorageCommand extends BaseCommand {
    
    private final String name;
    private final StorageType type;
    
    public SearchStorageCommand(String name, StorageType type) {
        super(CommandType.SEARCH_STORAGE);
        this.name = name;
        this.type = type;
    }
}
