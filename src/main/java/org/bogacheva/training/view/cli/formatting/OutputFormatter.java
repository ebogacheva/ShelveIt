package org.bogacheva.training.view.cli.formatting;

import org.bogacheva.training.view.cli.execution.CommandExecutionResult;

/**
 * Interface for formatting command execution results.
 */
public interface OutputFormatter {
    
    /**
     * Formats and displays the command execution result.
     * 
     * @param result the command execution result to format
     */
    void formatAndDisplay(CommandExecutionResult result);
}
