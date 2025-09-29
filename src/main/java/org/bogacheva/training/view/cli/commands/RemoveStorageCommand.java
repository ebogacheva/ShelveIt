package org.bogacheva.training.view.cli.commands;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class RemoveStorageCommand extends BaseCommand {

    private final Long id;

    public RemoveStorageCommand(long id) {
        super(CommandType.REMOVE_STORAGE);
        this.id = id;
    }
}
