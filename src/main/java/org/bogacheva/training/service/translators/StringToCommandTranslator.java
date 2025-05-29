package org.bogacheva.training.service.translators;

import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.view.commands.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StringToCommandTranslator implements Translator<String, BaseCommand> {

    private static final Pattern WHOLE_COMMAND_PATTERN = Pattern.compile("\"([^\"]*)\"|(\\S+)");
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9 ]+");
    private static final Pattern ID_PATTERN = Pattern.compile("\\d+");

    private static final int COMMAND_0 = 0;
    private static final int COMMAND_1 = 1;
    private static final int STORAGE_NAME_START_INDEX = 3;
    private static final int ITEM_NAME_START_INDEX = 2;
    private static final int STORAGE_TYPE_INDEX = 2;
    private static final int PARENT_ID_INDEX = 4;
    private static final int STORAGE_ID_INDEX = 3;
    private static final int REMOVE_ID_INDEX = 2;

    private static final int CREATE_STORAGE_ARGS = 4;
    private static final int CREATE_ITEM_ARGS = 4;
    private static final int REMOVE_ARGS = 3;

    @Override
    public BaseCommand translate(String input) {
        String[] parts = splitInParts(input);
        CommandType command = CommandType.of(getCommand(parts));
        return switch (command) {
            case CREATE_STORAGE -> {
                validateArgs(parts, CREATE_STORAGE_ARGS);
                Long parentId = parseOptionalParentId(parts);
                String name = validateName(getName(parts, STORAGE_NAME_START_INDEX));
                StorageType type = validateType(parts[STORAGE_TYPE_INDEX]);
                yield new CreateStorageCommand(name, type, parentId);
            }
            case CREATE_ITEM -> {
                validateArgs(parts, CREATE_ITEM_ARGS);
                String name = validateName(getName(parts, ITEM_NAME_START_INDEX));
                Long storageId = validateId(parts[STORAGE_ID_INDEX]);
                List<String> keywords = Arrays.stream(parts[4].split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
                yield new CreateItemCommand(name, storageId, keywords);
            }
            case REMOVE_ITEM -> createRemoveCommand(parts, RemoveItemCommand::new);
            case REMOVE_STORAGE -> createRemoveCommand(parts, RemoveStorageCommand::new);
            case LIST_STORAGES -> new ListStorageCommand();
            case LIST_ITEMS -> new ListItemCommand();
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

    private Long parseOptionalParentId(String[] parts) {
        return (PARENT_ID_INDEX < parts.length) ? validateId(parts[PARENT_ID_INDEX]) : null;
    }

    private BaseCommand createRemoveCommand(String[] parts, Function<Long, BaseCommand> constructor) {
        validateArgs(parts, REMOVE_ARGS);
        return constructor.apply(validateId(parts[REMOVE_ID_INDEX]));
    }

    private void validateArgs(String[] parts, int expected) {
        if (parts.length < expected) {
            throw new IllegalArgumentException("Invalid number of arguments!");
        }
    }

    private String getName(String[] parts, int startIndex) {
        return parts[startIndex];
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

    private String getCommand(String[] parts) {
        if (parts == null || parts.length == 0) {
            return "";
        }
        if (parts.length > 1) {
            return parts[COMMAND_0] + "_" + parts[COMMAND_1];
        }
        return parts[COMMAND_0];
    }
}
