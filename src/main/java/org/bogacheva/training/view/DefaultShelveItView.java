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

    public void printHeader() {
        printer.println("Welcome to the ShelveIt App!");
        printer.println("Available commands:");

        printer.println("    list storages           - List all storages");
        printer.println("    list items              - List all items");
        printer.println("    create storage <type> <name> <parentId>   - Create a new storage");
        printer.println("    create item <name> <storageId> <keyword1,keyword2,...>   - Create a new item");
        printer.println("    remove storage <storageId>   - Remove a storage by its index");
        printer.println("    remove item <itemId>         - Remove an item by its index");
        printer.println("    get storage <storageId>      - Get storage details by ID");
        printer.println("    get item <itemId>            - Get item details by ID");
        printer.println("    list substorages <storageId> - List sub-storages of a given storage");
        printer.println("    get items by storage <storageId> - List items within a given storage");
        printer.println("    exit                         - Exit the application");

        printer.println("");
        printer.println("Notes:");
        printer.println("    <name> may contain letters, numbers, and spaces; wrap in quotation marks if needed.");
        printer.println("    <keywords> is a comma-separated list (e.g., book,reading,novel).");
        printer.println("    <type> is one of: RESIDENCE, ROOM, FURNITURE, UNIT.");
        printer.println("    <parentId> is the index of the parent storage in the list.");
        printer.println("    <storageId> is the index of the storage in the list.");
        printer.println("    <itemId> is the index of the item in the list.");
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
