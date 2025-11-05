package org.bogacheva.training.view.cli.formatting;

import org.bogacheva.training.view.cli.ShelveItView;
import org.bogacheva.training.service.execution.comexecutor.CommandExecutionResult;
import org.springframework.stereotype.Component;

/**
 * Default implementation of OutputFormatter.
 * Handles formatting and display of command execution results.
 */
@Component
public class DefaultOutputFormatter implements OutputFormatter {
    
    private final ShelveItView view;
    
    public DefaultOutputFormatter(ShelveItView view) {
        this.view = view;
    }
    
    @Override
    public void formatAndDisplay(CommandExecutionResult result) {
        if (result.hasMessage()) {
            view.printError(result.getMessage());
        }
        
        if (result.hasData()) {
            view.print(result.getData());
        }
    }
}
