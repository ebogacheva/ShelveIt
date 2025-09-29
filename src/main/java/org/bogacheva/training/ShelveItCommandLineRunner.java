package org.bogacheva.training;

import org.bogacheva.training.translation.StringToCommandTranslator;
import org.bogacheva.training.translation.Translator;
import org.bogacheva.training.view.cli.ShelveItView;
import org.bogacheva.training.view.cli.commands.BaseCommand;
import org.bogacheva.training.view.cli.execution.CommandExecutor;
import org.bogacheva.training.view.cli.execution.CommandExecutionResult;
import org.bogacheva.training.view.cli.formatting.OutputFormatter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner implementation for the ShelveIt application.
 * Handles reading user input, translating it to commands, and coordinating
 * command execution. 
 */
@Component
public class ShelveItCommandLineRunner implements CommandLineRunner {

    private final ShelveItView shelveItView;
    private final Translator<String, BaseCommand> translator;
    private final CommandExecutor commandExecutor;
    private final OutputFormatter outputFormatter;

    public ShelveItCommandLineRunner(
            ShelveItView shelveItView,
            StringToCommandTranslator translator,
            CommandExecutor commandExecutor,
            OutputFormatter outputFormatter) {
        this.shelveItView = shelveItView;
        this.translator = translator;
        this.commandExecutor = commandExecutor;
        this.outputFormatter = outputFormatter;
    }

    @Override
    public void run(String... args) {
        shelveItView.printHeader();
        boolean exitRequested = false;
        
        do {
            shelveItView.printPrompt();
            try {
                String userInput = shelveItView.readCommand();
                BaseCommand command = translator.translate(userInput);
                CommandExecutionResult result = commandExecutor.execute(command);
                outputFormatter.formatAndDisplay(result);
                exitRequested = result.shouldExit();
                
            } catch (Exception e) {
                shelveItView.printError(e.getMessage());
            }
        } while (!exitRequested);
        
        shelveItView.printExit();
    }

}
