package org.bogacheva.training.domain.storages.strategies;

import org.bogacheva.training.domain.storages.StorageType;

public interface StorageTypeStrategy {
    boolean canContain(StorageType type);
}
