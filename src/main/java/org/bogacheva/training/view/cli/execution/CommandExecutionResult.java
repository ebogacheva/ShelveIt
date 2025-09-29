package org.bogacheva.training.view.cli.execution;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

import java.util.List;

@Getter
@AllArgsConstructor
public class CommandExecutionResult {
    
    private final List<?> data;
    private final boolean shouldExit;
    private final String message;
    
    // Convenience constructors
    public CommandExecutionResult(List<?> data, boolean shouldExit) {
        this(data, shouldExit, null);
    }
    
    public CommandExecutionResult(boolean shouldExit) {
        this(null, shouldExit, null);
    }
    
    public CommandExecutionResult(boolean shouldExit, String message) {
        this(null, shouldExit, message);
    }
    
    // Utility methods
    public boolean hasData() {
        return data != null && !data.isEmpty();
    }
    
    public boolean hasMessage() {
        return message != null && !message.trim().isEmpty();
    }
}
