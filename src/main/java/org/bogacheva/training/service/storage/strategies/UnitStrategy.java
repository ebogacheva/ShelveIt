package org.bogacheva.training.service.storage.strategies;

import org.bogacheva.training.domain.storage.StorageType;

public class UnitStrategy implements StorageTypeStrategy {

    @Override
    public boolean canContain(StorageType type) {
        return false;
    }

    @Override
    public boolean canHaveParent() {
        return true;
    }
}
