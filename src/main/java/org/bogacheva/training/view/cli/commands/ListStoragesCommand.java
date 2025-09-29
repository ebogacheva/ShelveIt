package org.bogacheva.training.view.cli.commands;

import lombok.ToString;

@ToString
public final class ListStoragesCommand extends BaseCommand {
    public ListStoragesCommand() {
        super(CommandType.LIST_STORAGES);
    }
}
