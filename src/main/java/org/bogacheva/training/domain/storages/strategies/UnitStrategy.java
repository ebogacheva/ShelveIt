package org.bogacheva.training.domain.storages.strategies;

import org.bogacheva.training.domain.storages.StorageType;

public class UnitStrategy implements StorageTypeStrategy {

    @Override
    public boolean canContain(StorageType type) {
        return false;
    }
}
