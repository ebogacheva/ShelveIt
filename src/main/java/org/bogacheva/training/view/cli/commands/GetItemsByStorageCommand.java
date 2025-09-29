package org.bogacheva.training.view.cli.commands;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class GetItemsByStorageCommand extends BaseCommand {

    private final Long storageId;

    public GetItemsByStorageCommand(long id) {
        super(CommandType.GET_ITEMS_BY_STORAGE);
        this.storageId = id;
    }
}
