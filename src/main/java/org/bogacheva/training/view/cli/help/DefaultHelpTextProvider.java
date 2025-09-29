package org.bogacheva.training.view.cli.help;

import org.springframework.stereotype.Component;

/**
 * Default implementation of HelpTextProvider.
 */
@Component
public class DefaultHelpTextProvider implements HelpTextProvider {
    
    private static final String HELP_TEXT = """
            Welcome to the ShelveIt App!
            Available commands (optional parameters are given in [...] :
            
                list storages                          - List all storages
                list items                             - List all items
                create storage --type <type> --name <name> [--parent <parentId>]   - Create a new storage
                create item --name <name> --storage <storageId> [--keywords <keyword1,keyword2,...>]   
                        - Create a new item
                remove storage --id <storageId>        - Remove a storage by its ID
                remove item --id <itemId>              - Remove an item by its ID
                get storage --id <storageId>           - Get storage details by ID
                get item --id <itemId>                 - Get item details by ID
                list substorages --id <storageId>      - List sub-storages of a given storage
                get items by storage --id <storageId>  - List items within a given storage
                search item [--name <name>] [--keywords <keyword1,keyword2,...>]   - Search for items
                get items near --id <itemId>           - List items located near a given item
                track storages --id <itemId>           - Show full storage hierarchy path for a given item
                exit                                   - Exit the application
                help [<command>]                       - Show this help text or help for a specific command
            
            Notes:
                <name> may contain letters, numbers, and spaces; wrap in quotation marks if it 
                contains spaces or special characters (e.g., "box with buttons").
                <keywords> is a comma-separated list. Spaces between keywords are optional 
                (e.g., book,reading,novel or book, reading, novel).
                <type> is one of: RESIDENCE, ROOM, FURNITURE, UNIT.
                <parentId>, <storageId>, <itemId> are numeric IDs.
            """;
    
    @Override
    public String getHelpText() {
        return HELP_TEXT;
    }
    
    @Override
    public String getCommandHelp(String command) {
        return switch (command.toLowerCase()) {
            case "list storages" -> getListStoragesHelp();
            case "list items" -> getListItemsHelp();
            case "create storage" -> getCreateStorageHelp();
            case "create item" -> getCreateItemHelp();
            case "remove storage" -> getRemoveStorageHelp();
            case "remove item" -> getRemoveItemHelp();
            case "get storage" -> getGetStorageHelp();
            case "get item" -> getGetItemHelp();
            case "list substorages" -> getListSubStoragesHelp();
            case "get items by storage" -> getGetItemsByStorageHelp();
            case "search item" -> getSearchItemHelp();
            case "get items near" -> getGetItemsNearHelp();
            case "track storages" -> getTrackStoragesHelp();
            case "exit" -> getExitHelp();
            case "help" -> getHelpHelp();
            default -> "Unknown command. Use 'help' to see all available commands.";
        };
    }
    
    private String getListStoragesHelp() {
        return """
                LIST STORAGES
                
                Description: List all storages in the system
                
                Usage: list storages
                
                Options: None
                
                Example:
                    list storages
                """;
    }
    
    private String getListItemsHelp() {
        return """
                LIST ITEMS
                
                Description: List all items in the system
                
                Usage: list items
                
                Options: None
                
                Example:
                    list items
                """;
    }
    
    private String getCreateStorageHelp() {
        return """
                CREATE STORAGE
                
                Description: Create a new storage with specified type and name
                
                Usage: create storage --type <type> --name <name> [--parent <parentId>]
                
                Required Options:
                    --type <type>     Storage type (RESIDENCE, ROOM, FURNITURE, UNIT)
                    --name <name>     Storage name (letters, numbers, spaces allowed)
                
                Optional Options:
                    --parent <parentId>   Parent storage ID (numeric). 

                    RESIDENCE storage should not have a parent. 
                    ROOM, FURNITURE, UNIT storage should have a parent.
                
                Name Rules:
                    - May contain letters, numbers, and spaces
                    - Wrap in quotation marks if contains spaces or special characters
                    - Examples: "My Room", "Living Room", "Storage Box"
                
                Type Options:
                    - RESIDENCE: Top-level storage (house, apartment)
                    - ROOM: Room within a residence
                    - FURNITURE: Furniture within a room
                    - UNIT: Storage unit within furniture
                
                Examples:
                    create storage --type ROOM --name "Living Room"
                    create storage --type FURNITURE --name "Bookshelf" --parent 5
                    create storage --type UNIT --name "Top Shelf" --parent 10
                """;
    }
    
    private String getCreateItemHelp() {
        return """
                CREATE ITEM
                
                Description: Create a new item with specified name and storage location
                
                Usage: create item --name <name> --storage <storageId> [--keywords <keyword1,keyword2,...>]
                
                Required Options:
                    --name <name>         Item name (letters, numbers, spaces allowed)
                    --storage <storageId> Storage ID where item will be placed (numeric)
                
                Optional Options:
                    --keywords <keywords> Comma-separated list of keywords
                
                Name Rules:
                    - May contain letters, numbers, and spaces
                    - Wrap in quotation marks if contains spaces or special characters
                    - Examples: "My Book", "Red Shirt", "Gaming Laptop"
                
                Keywords Rules:
                    - Comma-separated list of keywords
                    - Spaces between keywords are optional
                    - Examples: "book,reading,novel" or "book, reading, novel"
                
                Examples:
                    create item --name "Programming Book" --storage 5 --keywords "book,programming,tech"
                    create item --name "Coffee Mug" --storage 10 --keywords "kitchen,drink,ceramic"
                    create item --name "Laptop" --storage 15
                """;
    }
    
    private String getRemoveStorageHelp() {
        return """
                REMOVE STORAGE
                
                Description: Remove a storage by its ID
                
                Usage: remove storage --id <storageId>
                
                Required Options:
                    --id <storageId>  Storage ID to remove (numeric)
                
                Important Notes:
                    - This will also remove all sub-storages and items within this storage
                    - This action cannot be undone
                
                Examples:
                    remove storage --id 5
                    remove storage --id 10
                """;
    }
    
    private String getRemoveItemHelp() {
        return """
                REMOVE ITEM
                
                Description: Remove an item by its ID
                
                Usage: remove item --id <itemId>
                
                Required Options:
                    --id <itemId>    Item ID to remove (numeric)
                
                Important Notes:
                    - This action cannot be undone

                Examples:
                    remove item --id 25
                    remove item --id 100
                """;
    }
    
    private String getGetStorageHelp() {
        return """
                GET STORAGE
                
                Description: Get detailed information about a storage
                
                Usage: get storage --id <storageId>
                
                Required Options:
                    --id <storageId>  Storage ID to retrieve (numeric)
                
                Returns:
                    - Storage details (ID, name, type, parent)
                    - List of sub-storages
                    - List of items in this storage
                
                Examples:
                    get storage --id 5
                    get storage --id 10
                """;
    }
    
    private String getGetItemHelp() {
        return """
                GET ITEM
                
                Description: Get detailed information about an item
                
                Usage: get item --id <itemId>
                
                Required Options:
                    --id <itemId>    Item ID to retrieve (numeric)
                
                Returns:
                    - Item details (ID, name, keywords)
                    - Storage location information
                
                Examples:
                    get item --id 25
                    get item --id 100
                """;
    }
    
    private String getListSubStoragesHelp() {
        return """
                LIST SUB-STORAGES
                
                Description: List all sub-storages of a given storage
                
                Usage: list substorages --id <storageId>
                
                Required Options:
                    --id <storageId>  Parent storage ID (numeric)
                
                Returns:
                    - List of all direct sub-storages
                    - Does not include items, only storage containers
                
                Examples:
                    list substorages --id 5
                    list substorages --id 10
                """;
    }
    
    private String getGetItemsByStorageHelp() {
        return """
                GET ITEMS BY STORAGE
                
                Description: List all items within a specific storage
                
                Usage: get items by storage --id <storageId>
                
                Required Options:
                    --id <storageId>  Storage ID to search in (numeric)
                
                Returns:
                    - List of all items directly in this storage
                    - Does not include items in sub-storages
                
                Examples:
                    get items by storage --id 5
                    get items by storage --id 10
                """;
    }
    
    private String getSearchItemHelp() {
        return """
                SEARCH ITEM
                
                Description: Search for items by name or keywords
                
                Usage: search item [--name <name>] [--keywords <keyword1,keyword2,...>]
                
                Options (at least one required):
                    --name <name>         Partial item name (case-insensitive)
                    --keywords <keywords> Comma-separated list of keywords
                
                Search Rules:
                    - Name search: Partial, case-insensitive match
                    - Keywords: Items must contain at least one of the specified keywords
                
                Examples:
                    search item --name "book"
                    search item --keywords "programming,tech"
                    search item --name "laptop" --keywords "gaming,computer"
                """;
    }
    
    private String getGetItemsNearHelp() {
        return """
                GET ITEMS NEAR
                
                Description: List items located near a given item
                
                Usage: get items near --id <itemId>
                
                Required Options:
                    --id <itemId>    Reference item ID (numeric)
                
                Returns:
                    - List of items in the same storage as the reference item
                    - Excludes the reference item itself
                
                Examples:
                    get items near --id 25
                    get items near --id 100
                """;
    }
    
    private String getTrackStoragesHelp() {
        return """
                TRACK STORAGES
                
                Description: Show the complete storage hierarchy path for an item
                
                Usage: track storages --id <itemId>
                
                Required Options:
                    --id <itemId>    Item ID to track (numeric)
                
                Returns:
                    - Complete hierarchy path from root storage to item location
                    - Shows storage IDs in order from top-level to item location
                
                Examples:
                    track storages --id 25
                    track storages --id 100
                """;
    }
    
    private String getExitHelp() {
        return """
                EXIT
                
                Description: Exit the application
                
                Usage: exit
                
                Options: None
                
                Example:
                    exit
                """;
    }
    
    private String getHelpHelp() {
        return """
                HELP
                
                Description: Show help information
                
                Usage: help [<command>]
                
                Options:
                    <command>    Optional command name for specific help
                
                Examples:
                    help                    # Show general help
                    help create storage     # Show help for create storage command
                    help search item        # Show help for search item command
                """;
    }
}
