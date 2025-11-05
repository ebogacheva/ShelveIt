package org.bogacheva.training.service.command;

import lombok.ToString;

@ToString
public final class ListStoragesCommand extends BaseCommand {
    public ListStoragesCommand() {
        super(CommandType.LIST_STORAGES);
    }
}
