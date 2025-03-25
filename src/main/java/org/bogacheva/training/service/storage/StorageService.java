package org.bogacheva.training.service.storage;

import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.mapper.ItemMapper;
import org.bogacheva.training.service.mapper.StorageMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StorageService {

    private static final AtomicLong counter = new AtomicLong(1);

    private final StorageRepository storageRepo;
    private final StorageMapper storageMapper;
    private final ItemMapper itemMapper;

    public StorageService(StorageRepository storageRepository, StorageMapper storageMapper, ItemMapper itemMapper) {
        this.storageRepo = storageRepository;
        this.storageMapper = storageMapper;
        this.itemMapper = itemMapper;
    }

    private static long getNextId() {
        return counter.getAndIncrement();
    }

    public void create(StorageCreateDTO storageCreateDTO) {
        Storage newStorage = buildStorageFromDTO(storageCreateDTO);
        storageRepo.add(newStorage);
    }

    public StorageDTO getById(Long storageId) {
        return storageMapper.toDTO(getStorageById(storageId));
    }

    public void delete(Long storageId) {
        storageRepo.remove(storageId);
    }

    public List<StorageDTO> getAll() {
        return storageMapper.toDTOList(storageRepo.getAll());
    }

    private Storage buildStorageFromDTO(StorageCreateDTO storageCreateDTO) {
        Storage parent = getParentStorage(storageCreateDTO.getParentId());
        checkStorageTypeAndThrowException(storageCreateDTO.getType(), parent);
        Storage newStorage = new Storage(storageCreateDTO.getName(), storageCreateDTO.getType(), parent);
        Long id = getNextId();
        newStorage.setId(id);
        return newStorage;
    }

    private void checkStorageTypeAndThrowException(StorageType type, Storage parent) {
        checkIfStorageRequiresParent(type, parent);
        checkIfStorageCannotHaveParent(type, parent);
        checkIfParentCanContainType(type, parent);
    }

    private void checkIfParentCanContainType(StorageType type, Storage parent) {
        if (parent != null && !parent.getType().getStrategy().canContain(type)) {
            throw new IllegalArgumentException(parent.getType() + " cannot contain " + type);
        }
    }

    private void checkIfStorageCannotHaveParent(StorageType type, Storage parent) {
        if (!type.getStrategy().canHaveParent() && parent != null) {
            throw new IllegalArgumentException(type + " storage cannot have a parent.");
        }
    }

    private void checkIfStorageRequiresParent(StorageType type, Storage parent) {
        if (type.getStrategy().canHaveParent() && parent == null) {
            throw new IllegalArgumentException(type + " storage requires a parent.");
        }
    }

    private Storage getStorageById(Long storageId) {
        return storageRepo.getById(storageId)
                .orElseThrow(() -> new NoSuchElementException("Storage was not found."));
    }

    private Storage getParentStorage(Long parentId) {
        return (parentId != null) ? getStorageById(parentId) : null;
    }

    private List<Item> getAllItems(Long storageId) {
        Storage storage = getStorageById(storageId);
        List<Item> allItems = new ArrayList<>(storage.getItems());
        storage.getSubStorages().forEach(sub -> allItems.addAll(getAllItems(sub.getId())));
        return allItems;
    }

    public List<ItemDTO> getAllItemDTOs(Long storageId) {
        return itemMapper.toDTOList(getAllItems(storageId));
    }
}
