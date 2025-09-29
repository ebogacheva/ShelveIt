package org.bogacheva.training.view.cli.commands;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class HelpCommand extends BaseCommand {
    
    private final String command;

    public HelpCommand() {
        super(CommandType.HELP);
        this.command = null;
    }
    
    public HelpCommand(String command) {
        super(CommandType.HELP);
        this.command = command;
    }

    public boolean isGeneralHelp() {
        return command == null;
    }
}
