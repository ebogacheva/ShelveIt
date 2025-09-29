package org.bogacheva.training.view.cli.creation;

import org.bogacheva.training.view.cli.commands.BaseCommand;
import org.bogacheva.training.view.cli.parsing.ParsedCommand;

/**
 * Interface for creating command objects.
 */
public interface CommandFactory {
    
    /**
     * Creates a command object from the parsed command.
     * 
     * @param parsedCommand the parsed command
     * @return the created command
     */
    BaseCommand createCommand(ParsedCommand parsedCommand);
}
