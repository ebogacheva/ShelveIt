package org.bogacheva.training.service.storage.strategies;

import org.bogacheva.training.domain.storage.StorageType;

public interface StorageTypeStrategy {
    boolean canContain(StorageType type);
    boolean canHaveParent();
}
