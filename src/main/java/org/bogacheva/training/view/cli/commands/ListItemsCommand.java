package org.bogacheva.training.view.cli.commands;

import lombok.ToString;

@ToString
public final class ListItemsCommand extends BaseCommand {
    public ListItemsCommand() {
        super(CommandType.LIST_ITEMS);
    }
}
