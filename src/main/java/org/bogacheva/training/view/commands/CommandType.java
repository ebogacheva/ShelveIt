package org.bogacheva.training.view.commands;

public enum CommandType {
    CREATE_STORAGE,
    CREATE_ITEM,
    REMOVE_ITEM,
    REMOVE_STORAGE,
    LIST_STORAGES,
    LIST_ITEMS,
    LIST_SUBSTORAGES,
    GET_ITEMS_BY_STORAGE,
    SEARCH_ITEMS,
    SEARCH_ITEMS_BY_KEYWORD,
    GET_ITEM,
    GET_STORAGE,

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
