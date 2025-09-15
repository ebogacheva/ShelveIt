package org.bogacheva.training.view.cli.commands;

import lombok.Getter;

@Getter
public class ListSubStoragesCommand extends BaseCommand {

    private final Long storageId;

    public ListSubStoragesCommand(long id) {
        super(CommandType.LIST_SUBSTORAGES);
        this.storageId = id;
    }
}
