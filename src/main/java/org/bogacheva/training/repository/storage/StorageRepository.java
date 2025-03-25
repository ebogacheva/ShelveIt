package org.bogacheva.training.repository.storage;

import org.bogacheva.training.domain.storage.Storage;

import java.util.List;
import java.util.Optional;

public interface StorageRepository {

    void add(Storage storage);
    void remove(Long storageId);
    List<Storage> getAll();
    Optional<Storage> getById(Long storageId);

}
