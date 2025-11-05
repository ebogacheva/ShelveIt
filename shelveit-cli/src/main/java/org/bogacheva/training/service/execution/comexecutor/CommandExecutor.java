package org.bogacheva.training.service.execution.comexecutor;

import org.bogacheva.training.service.command.BaseCommand;

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
