package org.bogacheva.training.service.command;

import lombok.ToString;

@ToString
public final class ExitCommand extends BaseCommand{
    public ExitCommand() {
        super(CommandType.EXIT);
    }
}
