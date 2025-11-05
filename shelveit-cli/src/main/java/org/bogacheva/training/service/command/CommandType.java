package org.bogacheva.training.service.command;

public enum CommandType {
    CREATE_STORAGE,
    CREATE_ITEM,
    REMOVE_ITEM,
    REMOVE_STORAGE,
    LIST_STORAGES,
    LIST_ITEMS,
    LIST_SUBSTORAGES,
    GET_ITEMS_BY_STORAGE,
    SEARCH_ITEM,
    SEARCH_STORAGE,
    GET_ITEM,
    GET_STORAGE,
    GET_ITEMS_NEAR,
    TRACK_STORAGES,
    HELP,

    EXIT,
    BROKEN;

    public static CommandType of(String value) {
        try {
            return CommandType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BROKEN;
        }
    }
}
