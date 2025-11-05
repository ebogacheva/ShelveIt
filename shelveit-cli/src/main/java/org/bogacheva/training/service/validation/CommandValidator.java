package org.bogacheva.training.service.validation;

import org.bogacheva.training.service.parsing.ParsedCommand;

/**
 * Interface for validating command arguments.
 */
public interface CommandValidator {
    
    /**
     * Validates the parsed command.
     * 
     * @param parsedCommand the parsed command to validate
     * @throws IllegalArgumentException if validation fails
     */
    void validate(ParsedCommand parsedCommand);
}
