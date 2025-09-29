package org.bogacheva.training.view.cli.commands;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class RemoveItemCommand extends BaseCommand {

    private final Long id;

    public RemoveItemCommand(long id) {
        super(CommandType.REMOVE_ITEM);
        this.id = id;
    }
}
