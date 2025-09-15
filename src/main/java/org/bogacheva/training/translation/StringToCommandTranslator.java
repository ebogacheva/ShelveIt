package org.bogacheva.training.translation;

import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.view.cli.commands.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Translates raw user input strings into BaseCommand objects.
 * Supports validation of names, IDs, types, and keyword parsing.
 */
@Component
public class StringToCommandTranslator implements Translator<String, BaseCommand> {

    private static final Pattern WHOLE_COMMAND_PATTERN = Pattern.compile("\"([^\"]*)\"|(\\S+)");
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9 ]+");
    private static final Pattern ID_PATTERN = Pattern.compile("\\d+");

    @Override
    public BaseCommand translate(String input) {
        try {
            String[] parts = splitInParts(input);
            CommandType commandType = parseCommandType(parts);
            int start = getArgsStartIndex(commandType);
            Map<String, String> args = parseArguments(Arrays.copyOfRange(parts, start, parts.length));

            return switch (commandType) {
                case CREATE_STORAGE -> parseCreateStorage(args);
                case CREATE_ITEM -> parseCreateItem(args);
                case REMOVE_ITEM -> parseRemoveItem(args);
                case REMOVE_STORAGE -> parseRemoveStorage(args);
                case GET_ITEM -> parseGetItemById(args);
                case GET_STORAGE -> parseGetStorageById(args);
                case SEARCH_ITEM -> parseSearchItem(args);
                case LIST_STORAGES -> parseListStorages(args);
                case LIST_ITEMS -> parseListItems(args);
                case LIST_SUBSTORAGES -> parseListSubStorages(args);
                case GET_ITEMS_BY_STORAGE -> parseGetItemsByStorageId(args);
                case GET_ITEMS_NEAR -> parseGetItemsNear(args);
                case TRACK_STORAGES -> parseTrackStorages(args);
                case EXIT -> new ExitCommand();
                default -> new BrokenCommand("Unknown command type.");
            };
        } catch (Exception e) {
            return new BrokenCommand(e.getMessage());
        }
    }

    private int getArgsStartIndex(CommandType commandType) {
        return switch (commandType) {
            case GET_ITEMS_BY_STORAGE -> 4;
            case GET_ITEMS_NEAR -> 3;
            default -> 2;
        };
    }

    private String[] splitInParts(String input) {
        List<String> parts = new ArrayList<>();
        Matcher m = WHOLE_COMMAND_PATTERN.matcher(input);
        while (m.find()) {
            // For quoted strings, use group 1; otherwise, use group 2
            parts.add(m.group(1) != null ? m.group(1) : m.group(2));
        }
        return parts.toArray(new String[0]);
    }

    private CommandType parseCommandType(String[] parts) {
        if (parts.length == 0) {
            return CommandType.BROKEN;
        }
        if (parts.length == 1) {
            return CommandType.of(parts[0]);
        }
        String cmdCandidate = (parts[0] + "_" + parts[1]).toUpperCase();
        return getCommandType(parts, cmdCandidate);
    }

    private CommandType getCommandType(String[] parts, String cmdCandidate) {
        if ("GET_ITEMS".equals(cmdCandidate)) {
            if (parts.length > 3 && parts[2].equalsIgnoreCase("by") && parts[3].equalsIgnoreCase("storage")) {
                return CommandType.of(cmdCandidate + "_BY_STORAGE");
            }
            if (parts.length > 2 && parts[2].equalsIgnoreCase("near")) {
                return CommandType.GET_ITEMS_NEAR;
            }
        }
        return CommandType.of(cmdCandidate);
    }

    private Map<String, String> parseArguments(String[] parts) {
        Map<String, String> args = new HashMap<>();
        String currentKey = null;

        for (String part : parts) {
            if (part.startsWith("--")) {
                currentKey = part.substring(2).toLowerCase(); // Remove "--" prefix
                args.put(currentKey, ""); // Initialize with empty value
            } else if (currentKey != null) {
                args.put(currentKey, args.get(currentKey).isEmpty()
                        ? part.trim()
                        : args.get(currentKey) + " " + part.trim());
            } else {
                throw new IllegalArgumentException("Unexpected argument: " + part);
            }
        }
        return args;
    }

    private BaseCommand parseCreateStorage(Map<String, String> args) {
        validateRequiredArgs(args, "type", "name");
        String type = validateType(args.get("type").toUpperCase());
        String name = validateName(args.get("name"));
        Long parentId = args.containsKey("parent") ? validateId(args.get("parent")) : null;
        return new CreateStorageCommand(name, StorageType.of(type), parentId);
    }

    private BaseCommand parseCreateItem(Map<String, String> args) {
        validateRequiredArgs(args, "name", "storage");
        String name = validateName(args.get("name"));
        Long storageId = validateId(args.get("storage"));
        List<String> keywords = args.containsKey("keywords") ? parseKeywords(args.get("keywords")) : Collections.emptyList();
        return new CreateItemCommand(name, storageId, keywords);
    }

    private BaseCommand parseRemoveItem(Map<String, String> args) {
        validateRequiredArgs(args, "id");
        Long id = validateId(args.get("id"));
        return new RemoveItemCommand(id);
    }

    private BaseCommand parseRemoveStorage(Map<String, String> args) {
        validateRequiredArgs(args, "id");
        Long id = validateId(args.get("id"));
        return new RemoveStorageCommand(id);
    }

    private BaseCommand parseGetItemById(Map<String, String> args) {
        validateRequiredArgs(args, "id");
        Long id = validateId(args.get("id"));
        return new GetItemByIdCommand(id);
    }

    private BaseCommand parseGetStorageById(Map<String, String> args) {
        validateRequiredArgs(args, "id");
        Long id = validateId(args.get("id"));
        return new GetStorageByIdCommand(id);
    }

    private BaseCommand parseListStorages(Map<String, String> args) {
        return new ListStoragesCommand();
    }

    private BaseCommand parseListItems(Map<String, String> args) {
        return new ListItemsCommand();
    }

    private BaseCommand parseListSubStorages(Map<String, String> args) {
        validateRequiredArgs(args, "id");
        Long storageId = validateId(args.get("id"));
        return new ListSubStoragesCommand(storageId);
    }

    private BaseCommand parseGetItemsByStorageId(Map<String, String> args) {
        validateRequiredArgs(args, "id");
        Long storageId = validateId(args.get("id"));
        return new GetItemsByStorageCommand(storageId);
    }

    private BaseCommand parseSearchItem(Map<String, String> args) {
        String name = args.getOrDefault("name", null);
        List<String> keywords = args.containsKey("keywords") ? parseKeywords(args.get("keywords")) : Collections.emptyList();
        return new SearchItemCommand(name, keywords);
    }

    private BaseCommand parseGetItemsNear(Map<String, String> args) {
        validateRequiredArgs(args, "id");
        Long itemId = validateId(args.get("id"));
        return new GetItemsNearCommand(itemId);
    }

    private BaseCommand parseTrackStorages(Map<String, String> args) {
        validateRequiredArgs(args, "id");
        Long itemId = validateId(args.get("id"));
        return new TrackStoragesHierarchyCommand(itemId);
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

    private String validateType(String storageType) {
        try {
            return StorageType.of(storageType).name();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid storage type: " + storageType);
        }
    }

    private String validateName(String name) {
        if (NAME_PATTERN.matcher(name).matches()) {
            return name;
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }

    private long validateId(String id) {
        if (ID_PATTERN.matcher(id).matches()) {
            return Long.parseLong(id);
        }
        throw new IllegalArgumentException("Invalid ID: " + id);
    }

    private void validateRequiredArgs(Map<String, String> args, String... requiredKeys) {
        for (String key : requiredKeys) {
            if (!args.containsKey(key)) {
                throw new IllegalArgumentException("Missing required argument: --" + key);
            }
        }
    }
}
