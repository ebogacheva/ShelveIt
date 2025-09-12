package org.bogacheva.training.view.commands;

import lombok.Getter;

@Getter
public class GetItemsByStorageCommand extends BaseCommand {

    private final long storageId;

    public GetItemsByStorageCommand(long id) {
        super(CommandType.GET_ITEMS_BY_STORAGE);
        this.storageId = id;
    }
}
