package org.bogacheva.training;

import org.bogacheva.training.ai.AiAssistant;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.translation.StringToCommandTranslator;
import org.bogacheva.training.translation.Translator;
import org.bogacheva.training.view.cli.ShelveItView;
import org.bogacheva.training.view.cli.commands.BaseCommand;
import org.bogacheva.training.view.cli.execution.CommandExecutor;
import org.bogacheva.training.view.cli.execution.CommandExecutionResult;
import org.bogacheva.training.view.cli.formatting.OutputFormatter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * CommandLineRunner implementation for the ShelveIt application.
 * Handles reading user input, translating it to commands, and coordinating
 * command execution. 
 */
@Component
@Profile("cli")
public class ShelveItCommandLineRunner implements CommandLineRunner {

    private final ShelveItView shelveItView;
    private final Translator<String, BaseCommand> translator;
    private final CommandExecutor commandExecutor;
    private final OutputFormatter outputFormatter;
    private final AiAssistant aiAssistant;

    Runnable exitAction = () -> System.exit(0);

    public ShelveItCommandLineRunner(
            ShelveItView shelveItView,
            StringToCommandTranslator translator,
            CommandExecutor commandExecutor,
            OutputFormatter outputFormatter,
            Optional<AiAssistant> aiAssistant) {
        this.shelveItView = shelveItView;
        this.translator = translator;
        this.commandExecutor = commandExecutor;
        this.outputFormatter = outputFormatter;
        this.aiAssistant = aiAssistant.orElse(null);
    }

    @Override
    public void run(String... args) {
        shelveItView.printHeader();
        boolean exitRequested = false;
        
        do {
            shelveItView.printPrompt();
            try {
                String userInput = shelveItView.readCommand();

                if (userInput != null && userInput.startsWith("?")) {
                    String query = userInput.substring(1).trim();
                    if (aiAssistant == null) {
                        shelveItView.printError("AI assistant is not configured");
                    } else {
                        List<ItemDTO> results = aiAssistant.findItems(query);
                        outputFormatter.formatAndDisplay(new CommandExecutionResult(results, false));
                    }
                } else {
                    BaseCommand command = translator.translate(userInput);
                    CommandExecutionResult result = commandExecutor.execute(command);
                    outputFormatter.formatAndDisplay(result);
                    exitRequested = result.isShouldExit();
                }

            } catch (Exception e) {
                shelveItView.printError(e.getMessage());
            }
        } while (!exitRequested);
        
        shelveItView.printExit();
        exitAction.run();
    }

}
