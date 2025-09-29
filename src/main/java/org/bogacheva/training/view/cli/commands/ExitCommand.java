package org.bogacheva.training.view.cli.commands;

import lombok.ToString;

@ToString
public final class ExitCommand extends BaseCommand{
    public ExitCommand() {
        super(CommandType.EXIT);
    }
}
