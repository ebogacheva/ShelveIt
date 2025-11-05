package org.bogacheva.training.domain.storage;

public enum StorageType {
    RESIDENCE,
    ROOM,
    FURNITURE,
    UNIT;

    public static StorageType of(String value) {
        for (StorageType type : StorageType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(value + " is not an existing storage type!");
    }
}
