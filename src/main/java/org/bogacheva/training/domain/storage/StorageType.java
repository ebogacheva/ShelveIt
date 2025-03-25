package org.bogacheva.training.domain.storage;

import org.bogacheva.training.service.storage.strategies.*;
import org.springframework.stereotype.Component;

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
}
