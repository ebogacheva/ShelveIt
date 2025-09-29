package org.bogacheva.training.view.cli;

import org.bogacheva.training.view.cli.help.HelpTextProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultShelveItView implements ShelveItView{

    private final Printer<String> printer;
    private final Reader<String> reader;
    private final HelpTextProvider helpTextProvider;

    public DefaultShelveItView(Printer<String> printer, Reader<String> reader, HelpTextProvider helpTextProvider) {
        this.printer = printer;
        this.reader = reader;
        this.helpTextProvider = helpTextProvider;
    }

    public void printHeader() {
        printer.println(helpTextProvider.getHelpText());
    }

    @Override
    public void printExit() {
        printer.println("Keeping an eye on your things! Good bye!");
    }

    @Override
    public void printPrompt() {
        printer.print("> ");
    }

    @Override
    public <T> void print(List<T> things) {
        for (T t : things) {
            printer.println(t.toString());
        }
    }

    public String readCommand() {
        return reader.read();
    }

    public void printError(String message) {
        printer.println(message);
    }
}
