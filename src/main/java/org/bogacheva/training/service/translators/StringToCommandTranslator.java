package org.bogacheva.training.service.translators;

import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.view.commands.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StringToCommandTranslator implements Translator<String, BaseCommand> {

    private static final Pattern WHOLE_COMMAND_PATTERN = Pattern.compile("\"([^\"]*)\"|(\\S+)");
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9 ]+");
    private static final Pattern ID_PATTERN = Pattern.compile("\\d+");

    private static final int MIN_ARGS_CREATE_STORAGE = 4;
    private static final int MIN_ARGS_CREATE_ITEM = 5;
    private static final int MIN_ARGS_REMOVE = 3;
    private static final int MIN_ARGS_GET = 3;
    private static final int MIN_ARGS_LIST = 2;
    private static final int MIN_ARGS_SEARCH = 3;

    private static final int IDX_COMMAND_0 = 0;
    private static final int IDX_COMMAND_1 = 1;

    private static final int IDX_STORAGE_TYPE = 2;
    private static final int IDX_STORAGE_NAME = 3;
    private static final int IDX_PARENT_ID = 4;

    private static final int IDX_ITEM_NAME = 2;
    private static final int IDX_ITEM_STORAGE_ID = 3;
    private static final int IDX_ITEM_KEYWORDS = 4;

    private static final int IDX_REMOVE_ID = 2;
    private static final int IDX_GET_ID = 2;

    @Override
    public BaseCommand translate(String input) {
        String[] parts = splitInParts(input);
        CommandType command = parseCommandType(parts);
        return switch (command) {
            case CREATE_STORAGE -> parseCreateStorage(parts);
            case CREATE_ITEM -> parseCreateItem(parts);
            case REMOVE_ITEM -> parseRemoveItem(parts);
            case REMOVE_STORAGE -> parseRemoveStorage(parts);
            case GET_ITEM -> parseGetItemById(parts);
            case GET_STORAGE -> parseGetStorageById(parts);
            case SEARCH_ITEMS -> parseSearchItems(parts);
            case SEARCH_ITEMS_BY_KEYWORD -> parseSearchItemsByKeyword(parts);
            case LIST_STORAGES -> parseListStorages(parts);
            case LIST_ITEMS -> parseListItems(parts);
            case EXIT -> new ExitCommand();
            default -> new BrokenCommand();
        };
    }

    private String[] splitInParts(String input) {
        List<String> parts = new ArrayList<>();
        Matcher m = WHOLE_COMMAND_PATTERN.matcher(input);
        while (m.find()) {
            parts.add(m.group(1) != null ? m.group(1) : m.group(2));
        }
        return parts.toArray(new String[0]);
    }


    private BaseCommand parseCreateStorage(String[] parts) {
        final int expectedArgs = MIN_ARGS_CREATE_STORAGE;
        validateArgs(parts, expectedArgs);
        Long parentId = parseOptionalParentId(parts);
        String name = validateName(parts[IDX_STORAGE_NAME]);
        StorageType type = validateType(parts[IDX_STORAGE_TYPE]);
        return new CreateStorageCommand(name, type, parentId);
    }

    private BaseCommand parseCreateItem(String[] parts) {
        final int expectedArgs = MIN_ARGS_CREATE_ITEM;
        validateArgs(parts, expectedArgs);
        String name = validateName(parts[IDX_ITEM_NAME]);
        Long storageId = validateId(parts[IDX_ITEM_STORAGE_ID]);
        List<String> keywords = parseKeywords(parts[IDX_ITEM_KEYWORDS]);
        return new CreateItemCommand(name, storageId, keywords);
    }

    private BaseCommand parseRemoveItem(String[] parts) {
        final int expectedArgs = MIN_ARGS_REMOVE;
        validateArgs(parts, expectedArgs);
        long id = validateId(parts[IDX_REMOVE_ID]);
        return new RemoveItemCommand(id);
    }

    private BaseCommand parseRemoveStorage(String[] parts) {
        final int expectedArgs = MIN_ARGS_REMOVE;
        validateArgs(parts, expectedArgs);
        long id = validateId(parts[IDX_REMOVE_ID]);
        return new RemoveStorageCommand(id);
    }

    private BaseCommand parseGetItemById(String[] parts) {
        validateArgs(parts, MIN_ARGS_GET);
        long id = validateId(parts[IDX_GET_ID]);
        return new GetItemByIdCommand(id);
    }

    private BaseCommand parseGetStorageById(String[] parts) {
        validateArgs(parts, MIN_ARGS_GET);
        long id = validateId(parts[IDX_GET_ID]);
        return new GetStorageByIdCommand(id);
    }

    private BaseCommand parseSearchItems(String[] parts) {
        validateArgs(parts, MIN_ARGS_SEARCH);
        String searchTerm = parts[2];
        return new SearchItemsCommand(searchTerm);
    }

    private BaseCommand parseSearchItemsByKeyword(String[] parts) {
        validateArgs(parts, MIN_ARGS_SEARCH);
        String keyword = parts[2];
        return new SearchItemsByKeywordsCommand(keyword);
    }

    private BaseCommand parseListStorages(String[] parts) {
        validateArgs(parts, MIN_ARGS_LIST);
        return new ListStorageCommand();
    }

    private BaseCommand parseListItems(String[] parts) {
        validateArgs(parts, MIN_ARGS_LIST);
        return new ListItemCommand();
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

    private Long parseOptionalParentId(String[] parts) {
        return (IDX_PARENT_ID < parts.length) ? validateId(parts[IDX_PARENT_ID]) : null;
    }

    private void validateArgs(String[] parts, int expected) {
        if (parts.length < expected) {
            throw new IllegalArgumentException("Invalid number of arguments!");
        }
    }

    private String validateName(String name) {
        if (NAME_PATTERN.matcher(name).matches()) {
            return name;
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }

    private StorageType validateType(String storageType) {
        return StorageType.of(storageType);
    }

    private long validateId(String id) {
        if (ID_PATTERN.matcher(id).matches()) {
            return Long.parseLong(id);
        }
        throw new IllegalArgumentException("Invalid ID: " + id);
    }

    private CommandType parseCommandType(String[] parts) {
        if (parts.length == 0) {
            return CommandType.BROKEN;
        }
        String cmd;
        if (parts.length > 1) {
            cmd = (parts[IDX_COMMAND_0] + "_" + parts[IDX_COMMAND_1]).toUpperCase();
        } else {
            cmd = parts[IDX_COMMAND_0].toUpperCase();
        }
        try {
            return CommandType.valueOf(cmd);
        } catch (IllegalArgumentException e) {
            return CommandType.BROKEN;
        }
    }
}
