package org.bogacheva.training.domain.storage;

import org.bogacheva.training.service.storage.strategies.*;

import java.util.EnumMap;
import java.util.Map;

public enum StorageType {
    RESIDENCE,
    ROOM,
    FURNITURE,
    UNIT;

    private static final Map<StorageType, StorageTypeStrategy> STRATEGY_MAP = new EnumMap<>(StorageType.class);

    static {
        STRATEGY_MAP.put(RESIDENCE, new ResidenceStrategy());
        STRATEGY_MAP.put(ROOM, new RoomStrategy());
        STRATEGY_MAP.put(FURNITURE, new FurnitureStrategy());
        STRATEGY_MAP.put(UNIT, new UnitStrategy());
    }

    public StorageTypeStrategy getStrategy() {
        return STRATEGY_MAP.get(this);
    }

    public static StorageType of(String value) {
        for (StorageType type : StorageType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(value + " is not an existing storage type!");
    }
}
