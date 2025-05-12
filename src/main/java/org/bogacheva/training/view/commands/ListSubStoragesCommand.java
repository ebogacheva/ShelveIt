package org.bogacheva.training.view.commands;

import lombok.Getter;

@Getter
public class ListSubStoragesCommand extends BaseCommand {

    private final long storageId;

    public ListSubStoragesCommand(long id) {
        super(CommandType.LIST_SUBSTORAGES);
        this.storageId = id;
    }
}
