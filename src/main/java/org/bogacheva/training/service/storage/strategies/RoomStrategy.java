package org.bogacheva.training.service.storage.strategies;

import org.bogacheva.training.domain.storage.StorageType;

import java.util.EnumSet;

public class RoomStrategy implements StorageTypeStrategy {

    private static final EnumSet<StorageType> ALLOWED_TYPES =
            EnumSet.of(StorageType.FURNITURE, StorageType.UNIT);

    @Override
    public boolean canContain(StorageType type) {
        return ALLOWED_TYPES.contains(type);
    }

    @Override
    public boolean canHaveParent() {
        return true;
    }
}
