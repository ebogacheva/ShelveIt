package org.bogacheva.training.view.cli.execution;

import org.bogacheva.training.view.cli.commands.BaseCommand;

/**
 * Interface for executing commands.
 */
public interface CommandExecutor {
    
    /**
     * Executes a command and returns the result.
     * 
     * @param command the command to execute
     * @return the execution result
     */
    CommandExecutionResult execute(BaseCommand command);
}
