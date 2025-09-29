package org.bogacheva.training.view.cli.execution;

import java.util.List;

/**
 * Represents the result of a command execution.
 */
public class CommandExecutionResult {
    
    private final List<?> data;
    private final boolean shouldExit;
    private final String message;
    
    public CommandExecutionResult(List<?> data, boolean shouldExit, String message) {
        this.data = data;
        this.shouldExit = shouldExit;
        this.message = message;
    }
    
    public CommandExecutionResult(List<?> data, boolean shouldExit) {
        this(data, shouldExit, null);
    }
    
    public CommandExecutionResult(boolean shouldExit) {
        this(null, shouldExit, null);
    }
    
    public CommandExecutionResult(boolean shouldExit, String message) {
        this(null, shouldExit, message);
    }
    
    public List<?> getData() {
        return data;
    }
    
    public boolean shouldExit() {
        return shouldExit;
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean hasData() {
        return data != null && !data.isEmpty();
    }
    
    public boolean hasMessage() {
        return message != null && !message.trim().isEmpty();
    }
}
