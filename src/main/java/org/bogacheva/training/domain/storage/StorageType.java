package org.bogacheva.training.domain.storage;

import org.bogacheva.training.service.storage.strategies.*;

public enum StorageType {
    RESIDENCE(new ResidenceStrategy()),
    ROOM(new RoomStrategy()),
    FURNITURE(new FurnitureStrategy()),
    UNIT(new UnitStrategy());

    private final StorageTypeStrategy strategy;

    StorageType(StorageTypeStrategy strategy) {
        this.strategy = strategy;
    }

    public StorageTypeStrategy getStrategy() {
        return strategy;
    }
}
