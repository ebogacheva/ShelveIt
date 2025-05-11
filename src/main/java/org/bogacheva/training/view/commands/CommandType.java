package org.bogacheva.training.view.commands;

public enum CommandType {
    BROKEN(""),
    CREATE_STORAGE("create_storage"),
    CREATE_ITEM("create_item"),
    LIST_STORAGES("list_storages"),
    LIST_ITEMS("list_items"),
    REMOVE_ITEM("remove_item"),
    REMOVE_STORAGE("remove_storage"),
    EXIT("exit");

    private final String value;

    CommandType(String value) {
        this.value = value;
    }

    public static CommandType of(String value) {
        for (CommandType commandType : CommandType.values()) {
            if (commandType.value.equalsIgnoreCase(value)) {
                return commandType;
            }
        }
        return BROKEN;
    }
}
