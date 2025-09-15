package org.bogacheva.training.view.cli;

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
        printer.println("Available commands (optional parameters are given in [...] :");

        printer.println("    list storages                          - List all storages");
        printer.println("    list items                             - List all items");
        printer.println("    create storage --type <type> --name <name> [--parent <parentId>]   - Create a new storage");
        printer.println("    create item --name <name> --storage <storageId> [--keywords <keyword1,keyword2,...>]   " +
                "- Create a new item");
        printer.println("    remove storage --id <storageId>        - Remove a storage by its ID");
        printer.println("    remove item --id <itemId>              - Remove an item by its ID");
        printer.println("    get storage --id <storageId>           - Get storage details by ID");
        printer.println("    get item --id <itemId>                 - Get item details by ID");
        printer.println("    list substorages --id <storageId>      - List sub-storages of a given storage");
        printer.println("    get items by storage --id <storageId>  - List items within a given storage");
        printer.println("    search item [--name <name>] [--keywords <keyword1,keyword2,...>]   - Search for items");
        printer.println("    get items near --id <itemId>           - List items located near a given item");
        printer.println("    track storages --id <itemId>           - Show full storage hierarchy path for a given item");
        printer.println("    exit                                   - Exit the application");

        printer.println("");
        printer.println("Notes:");
        printer.println("    <name> may contain letters, numbers, and spaces; wrap in quotation marks if it " +
                "contains spaces or special characters (e.g., \"box with buttons\").");
        printer.println("    <keywords> is a comma-separated list. Spaces between keywords are optional " +
                "(e.g., book,reading,novel or book, reading, novel).");
        printer.println("    <type> is one of: RESIDENCE, ROOM, FURNITURE, UNIT.");
        printer.println("    <parentId>, <storageId>, <itemId> are numeric IDs.");
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
