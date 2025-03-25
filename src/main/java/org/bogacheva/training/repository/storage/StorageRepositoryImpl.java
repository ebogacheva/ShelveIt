package org.bogacheva.training.repository.storage;

import org.bogacheva.training.domain.storage.Storage;

import java.util.*;

public class StorageRepositoryImpl implements StorageRepository {

    private final Map<Long, Storage> storages = new HashMap<>();

    @Override
    public void add(Storage storage) {
        storages.put(storage.getId(), storage);
    }

    @Override
    public void remove(Long storageId) {
        storages.remove(storageId);
    }

    @Override
    public List<Storage> getAll() {
        return new ArrayList<>(storages.values());
    }

    @Override
    public Optional<Storage> getById(Long storageId) {
        return Optional.of(storages.get(storageId));
    }
}
