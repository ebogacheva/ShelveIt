package org.bogacheva.training.translation;

import org.bogacheva.training.view.cli.commands.BaseCommand;
import org.bogacheva.training.view.cli.creation.CommandFactory;
import org.bogacheva.training.view.cli.parsing.CommandParser;
import org.bogacheva.training.view.cli.validation.CommandValidator;
import org.springframework.stereotype.Component;

/**
 * Translates raw user input strings into BaseCommand objects.
 * Coordinates parsing, validation, and command creation using dedicated components.
 */
@Component
public class StringToCommandTranslator implements Translator<String, BaseCommand> {

    private final CommandParser commandParser;
    private final CommandValidator commandValidator;
    private final CommandFactory commandFactory;

    public StringToCommandTranslator(CommandParser commandParser, 
                                    CommandValidator commandValidator, 
                                    CommandFactory commandFactory) {
        this.commandParser = commandParser;
        this.commandValidator = commandValidator;
        this.commandFactory = commandFactory;
    }

    @Override
    public BaseCommand translate(String input) {
        try {
            var parsedCommand = commandParser.parse(input);
            commandValidator.validate(parsedCommand);
            return commandFactory.createCommand(parsedCommand);
        } catch (Exception e) {
            return new org.bogacheva.training.view.cli.commands.BrokenCommand(e.getMessage());
        }
    }

}
