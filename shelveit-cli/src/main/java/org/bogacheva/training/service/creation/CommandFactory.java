package org.bogacheva.training.service.creation;

import org.bogacheva.training.service.command.BaseCommand;
import org.bogacheva.training.service.parsing.ParsedCommand;

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
