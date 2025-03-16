package org.bogacheva.training.domain.storages;

import org.bogacheva.training.domain.storages.strategies.*;

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
