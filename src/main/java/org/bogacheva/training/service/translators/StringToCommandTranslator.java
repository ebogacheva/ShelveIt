package org.bogacheva.training.service.translators;

import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.view.commands.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Pattern;

@Component
public class StringToCommandTranslator implements Translator<String, BaseCommand> {

    private static final int COMMAND_0 = 0;
    private static final int COMMAND_1 = 1;
    private static final int CREATE_STORAGE_ARG_NUM = 4;
    private static final int STORAGE_NAME_START_INDEX = CREATE_STORAGE_ARG_NUM;
    private static final int CREATE_ITEM_ARG_NUM = 4;
    private static final int ITEM_NAME_START_INDEX = CREATE_ITEM_ARG_NUM - 1;
    private static final int STORAGE_TYPE_INDEX = 2;
    private static final int PARENT_ID_INDEX = 3;
    private static final int STORAGE_ID_INDEX = 2;
    private static final int REMOVE_ARG_NUM = 3;
    private static final int REMOVE_ID = 2;


    @Override
    public BaseCommand translate(String value) {
        String[] parts = value.split(" ");
        CommandType command = CommandType.of(getCommand(parts));
        long id = 0L;
        return switch (command) {
            case CREATE_STORAGE -> {
                validateCommandArgumentNumber(parts, CREATE_STORAGE_ARG_NUM);
                Long parentId = parseOptionalParentId(parts);
                String name = validateName(getName(parts, STORAGE_NAME_START_INDEX));
                StorageType type = validateType(parts[STORAGE_TYPE_INDEX]);
                yield new CreateStorageCommand(name, type, parentId);
            }
            case CREATE_ITEM -> {
                validateCommandArgumentNumber(parts, CREATE_ITEM_ARG_NUM);
                String name = validateName(getName(parts, ITEM_NAME_START_INDEX));
                Long storageId = validateId(parts[STORAGE_ID_INDEX]);
                yield new CreateItemCommand(name, storageId);
            }
            case LIST_STORAGES -> new ListStorageCommand();
            case LIST_ITEMS -> new ListItemCommand();
            case REMOVE_ITEM -> createRemoveCommand(parts, RemoveItemCommand::new);
            case REMOVE_STORAGE -> createRemoveCommand(parts, RemoveStorageCommand::new);
            case EXIT -> new ExitCommand();
            default -> new BrokenCommand();
        };
    }

    private Long parseOptionalParentId(String[] parts) {
        try {
            return validateId(parts[PARENT_ID_INDEX]);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private BaseCommand createRemoveCommand(String[] parts, Function<Long, BaseCommand> commandConstructor) {
        validateCommandArgumentNumber(parts, REMOVE_ARG_NUM);
       long id = validateId(parts[REMOVE_ID]);
       return commandConstructor.apply(id);
    }

    private void validateCommandArgumentNumber(String[] parts, int expected) {
        if (parts.length < expected) {
            throw new IllegalArgumentException("Invalid number of arguments!");
        }
    }

    private String getName(String[] parts, int start_index) {
        return String.join(" ", Arrays.copyOfRange(parts, STORAGE_NAME_START_INDEX, parts.length));
    }

    private String validateName(String name) {
        if (Pattern.compile("[a-zA-Z0-9 ]+").matcher(name).matches()) {
            return name;
        } else {
            throw new IllegalArgumentException(name + " is not valid!");
        }
    }

    private StorageType validateType(String storageType) {
        return StorageType.valueOf(storageType);
    }

    private long validateId(String id) {
        if (Pattern.compile("[0-9]+").matcher(id).matches()) {
            return Long.parseLong(id);
        }
        throw new IllegalArgumentException(id + " is not valid ID!");
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
