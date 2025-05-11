package org.bogacheva.training.view;

import org.bogacheva.training.domain.item.Item;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultShelveItView implements ShelveItView{

    private final Printer<String> printer;
    private final Reader<String> reader;

    public DefaultShelveItView(Printer<String> printer, Reader<String> reader) {
        this.printer = printer;
        this.reader = reader;
    }

    @Override
    public void printHeader() {
        printer.println("Welcome to the ShelveIt App!");
        printer.println("Available commands:");
        printer.println("    list storages");
        printer.println("    list items");
        printer.println("    create storage <type> <parentId> <name>");
        printer.println("    create item <storageId> <name>");
        printer.println("    exit");
        printer.println("where <name> may contain letters, numbers and spaces.");
        printer.println("where <type> is one of: RESIDENCE, ROOM, FURNITURE, UNIT.");
        printer.println("where <parentId> is the index of parent storage in the list.");
        printer.println("where <storageId> is the index of storage in the list.");
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
