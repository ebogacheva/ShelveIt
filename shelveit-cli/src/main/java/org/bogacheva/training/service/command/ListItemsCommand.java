package org.bogacheva.training.service.command;

import lombok.ToString;

@ToString
public final class ListItemsCommand extends BaseCommand {
    public ListItemsCommand() {
        super(CommandType.LIST_ITEMS);
    }
}
