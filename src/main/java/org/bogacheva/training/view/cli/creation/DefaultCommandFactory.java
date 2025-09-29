package org.bogacheva.training.view.cli.creation;

import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.view.cli.commands.*;
import org.bogacheva.training.view.cli.parsing.ParsedCommand;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of CommandFactory.
 */
@Component
public class DefaultCommandFactory implements CommandFactory {
    
    @Override
    public BaseCommand createCommand(ParsedCommand parsedCommand) {
        String commandType = parsedCommand.commandType();
        Map<String, String> args = parsedCommand.arguments();
        
        return switch (commandType) {
            case "create storage" -> createCreateStorageCommand(args);
            case "create item" -> createCreateItemCommand(args);
            case "remove item" -> createRemoveItemCommand(args);
            case "remove storage" -> createRemoveStorageCommand(args);
            case "get item" -> createGetItemByIdCommand(args);
            case "get storage" -> createGetStorageByIdCommand(args);
            case "search item" -> createSearchItemCommand(args);
            case "search storage" -> createSearchStorageCommand(args);
            case "list storages" -> new ListStoragesCommand();
            case "list items" -> new ListItemsCommand();
            case "list substorages" -> createListSubStoragesCommand(args);
            case "get items by storage" -> createGetItemsByStorageCommand(args);
            case "get items near" -> createGetItemsNearCommand(args);
            case "track storages" -> createTrackStoragesCommand(args);
            case "help" -> createHelpCommand(parsedCommand);
            case "exit" -> new ExitCommand();
            default -> new BrokenCommand("Unknown command type: " + commandType);
        };
    }
    
    private BaseCommand createCreateStorageCommand(Map<String, String> args) {
        String type = args.get("type").toUpperCase();
        String name = args.get("name");
        Long parentId = args.containsKey("parent") ? Long.parseLong(args.get("parent")) : null;
        return new CreateStorageCommand(name, StorageType.of(type), parentId);
    }
    
    private BaseCommand createCreateItemCommand(Map<String, String> args) {
        String name = args.get("name");
        Long storageId = Long.parseLong(args.get("storage"));
        List<String> keywords = args.containsKey("keywords") ? parseKeywords(args.get("keywords")) : Collections.emptyList();
        return new CreateItemCommand(name, storageId, keywords);
    }
    
    private BaseCommand createRemoveItemCommand(Map<String, String> args) {
        Long id = Long.parseLong(args.get("id"));
        return new RemoveItemCommand(id);
    }
    
    private BaseCommand createRemoveStorageCommand(Map<String, String> args) {
        Long id = Long.parseLong(args.get("id"));
        return new RemoveStorageCommand(id);
    }
    
    private BaseCommand createGetItemByIdCommand(Map<String, String> args) {
        Long id = Long.parseLong(args.get("id"));
        return new GetItemByIdCommand(id);
    }
    
    private BaseCommand createGetStorageByIdCommand(Map<String, String> args) {
        Long id = Long.parseLong(args.get("id"));
        return new GetStorageByIdCommand(id);
    }
    
    private BaseCommand createListSubStoragesCommand(Map<String, String> args) {
        Long storageId = Long.parseLong(args.get("id"));
        return new ListSubStoragesCommand(storageId);
    }
    
    private BaseCommand createGetItemsByStorageCommand(Map<String, String> args) {
        Long storageId = Long.parseLong(args.get("id"));
        return new GetItemsByStorageCommand(storageId);
    }
    
    private BaseCommand createSearchItemCommand(Map<String, String> args) {
        String name = args.getOrDefault("name", null);
        List<String> keywords = args.containsKey("keywords") ? parseKeywords(args.get("keywords")) : Collections.emptyList();
        return new SearchItemCommand(name, keywords);
    }
    
    private BaseCommand createSearchStorageCommand(Map<String, String> args) {
        String name = args.getOrDefault("name", null);
        StorageType type = args.containsKey("type") ? StorageType.of(args.get("type").toUpperCase()) : null;
        return new SearchStorageCommand(name, type);
    }
    
    private BaseCommand createGetItemsNearCommand(Map<String, String> args) {
        Long itemId = Long.parseLong(args.get("id"));
        return new GetItemsNearCommand(itemId);
    }
    
    private BaseCommand createTrackStoragesCommand(Map<String, String> args) {
        Long itemId = Long.parseLong(args.get("id"));
        return new TrackStoragesHierarchyCommand(itemId);
    }
    
    private BaseCommand createHelpCommand(ParsedCommand parsedCommand) {
        String[] parts = parsedCommand.commandParts();
        if (parts.length > 1) {
            // Join all parts after "help" to get the full command name
            String command = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
            return new HelpCommand(command);
        } else {
            // show general help
            return new HelpCommand();
        }
    }
    
    private List<String> parseKeywords(String keywordsStr) {
        if (keywordsStr == null || keywordsStr.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(keywordsStr.split("[,\\s]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
