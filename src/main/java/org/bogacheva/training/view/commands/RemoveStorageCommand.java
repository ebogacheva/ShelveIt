package org.bogacheva.training.view.commands;

import lombok.Getter;

@Getter
public class RemoveStorageCommand extends BaseCommand {

    private final long id;

    public RemoveStorageCommand(long id) {
        super(CommandType.REMOVE_STORAGE);
        this.id = id;
    }
}
