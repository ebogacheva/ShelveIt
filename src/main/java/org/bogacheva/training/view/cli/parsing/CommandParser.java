package org.bogacheva.training.view.cli.parsing;


/**
 * Interface for parsing command arguments.
 */
public interface CommandParser {
    
    /**
     * Parses the input string into command parts.
     * 
     * @param input the raw input string
     * @return parsed command parts
     */
    ParsedCommand parse(String input);
}

