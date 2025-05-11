package org.bogacheva.training.view.commands;

import lombok.Getter;

@Getter
public class RemoveItemCommand extends BaseCommand {

    private final long id;

    public RemoveItemCommand(long id) {
        super(CommandType.REMOVE_ITEM);
        this.id = id;
    }
}
