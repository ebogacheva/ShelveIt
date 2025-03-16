package org.bogacheva.training.domain.storages.strategies;

import org.bogacheva.training.domain.storages.StorageType;

import java.util.EnumSet;

public class ResidenceStrategy implements StorageTypeStrategy {

    private static final EnumSet<StorageType> ALLOWED_TYPES =
            EnumSet.of(StorageType.ROOM, StorageType.FURNITURE, StorageType.UNIT);

    @Override
    public boolean canContain(StorageType type) {
        return ALLOWED_TYPES.contains(type);
    }
}