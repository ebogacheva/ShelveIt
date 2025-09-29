package org.bogacheva.training.view.cli.commands;

import lombok.Getter;

@Getter
public abstract class BaseCommand {

    private final CommandType commandType;

    public BaseCommand(CommandType commandType) {
        this.commandType = commandType;
    }
}
