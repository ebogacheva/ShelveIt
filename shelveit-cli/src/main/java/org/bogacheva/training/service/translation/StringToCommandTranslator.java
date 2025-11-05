package org.bogacheva.training.service.translation;

import org.bogacheva.training.service.command.BaseCommand;
import org.bogacheva.training.service.command.BrokenCommand;
import org.bogacheva.training.service.creation.CommandFactory;
import org.bogacheva.training.service.parsing.CommandParser;
import org.bogacheva.training.service.validation.CommandValidator;
import org.springframework.stereotype.Component;

/**
 * Translates raw user input strings into BaseCommand objects.
 * Coordinates parsing, validation, and command creation.
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
            return new BrokenCommand(e.getMessage());
        }
    }

}
