package org.bogacheva.training.view.cli.validation;

import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.view.cli.parsing.ParsedCommand;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Default implementation of CommandValidator.
 */
@Component
public class DefaultCommandValidator implements CommandValidator {
    
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9 ]+");
    private static final Pattern ID_PATTERN = Pattern.compile("\\d+");
    
    @Override
    public void validate(ParsedCommand parsedCommand) {
        String commandType = parsedCommand.commandType();
        Map<String, String> args = parsedCommand.arguments();
        
        switch (commandType) {
            case "create storage" -> validateCreateStorage(args);
            case "create item" -> validateCreateItem(args);
            case "remove item", "remove storage", "get item", "get storage", 
                 "list substorages", "get items by storage", "get items near", 
                 "track storages" -> validateRequiredArgs(args, "id");
            case "search item" -> validateSearchItem(args);
            case "list storages", "list items", "exit", "help" -> {
                // No validation needed for these commands
            }
            default -> throw new IllegalArgumentException("Unknown command type: " + commandType);
        }
    }
    
    private void validateCreateStorage(Map<String, String> args) {
        validateRequiredArgs(args, "type", "name");
        validateType(args.get("type"));
        validateName(args.get("name"));
        if (args.containsKey("parent")) {
            validateId(args.get("parent"));
        }
    }
    
    private void validateCreateItem(Map<String, String> args) {
        validateRequiredArgs(args, "name", "storage");
        validateName(args.get("name"));
        validateId(args.get("storage"));
        if (args.containsKey("keywords")) {
            validateKeywords(args.get("keywords"));
        }
    }
    
    private void validateSearchItem(Map<String, String> args) {
        if (args.containsKey("name")) {
            validateName(args.get("name"));
        }
        if (args.containsKey("keywords")) {
            validateKeywords(args.get("keywords"));
        }
        if (args.isEmpty()) {
            throw new IllegalArgumentException("Search command requires at least one search criteria (--name or --keywords)");
        }
    }
    
    private void validateType(String storageType) {
        try {
            StorageType.of(storageType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid storage type: " + storageType);
        }
    }
    
    private void validateName(String name) {
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid name: " + name);
        }
    }
    
    private void validateId(String id) {
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
    }
    
    private void validateKeywords(String keywords) {
        if (keywords == null || keywords.isBlank()) {
            return; // Empty keywords are allowed
        }
    }
    
    private void validateRequiredArgs(Map<String, String> args, String... requiredKeys) {
        for (String key : requiredKeys) {
            if (!args.containsKey(key)) {
                throw new IllegalArgumentException("Missing required argument: --" + key);
            }
        }
    }
}
