package org.bogacheva.training.view.cli.commands;

public abstract class BaseCommand {

    private final CommandType commandType;

    public BaseCommand(CommandType commandType) {
        this.commandType = commandType;
    }

    public CommandType getCommandType() {
        return commandType;
    }

}
