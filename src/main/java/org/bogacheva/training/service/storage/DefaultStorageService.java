package org.bogacheva.training.service.storage;

import lombok.extern.slf4j.Slf4j;
import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.dto.StorageUpdateDTO;
import org.bogacheva.training.service.exceptions.StorageNotFoundException;
import org.bogacheva.training.service.mapper.ItemMapper;
import org.bogacheva.training.service.mapper.StorageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for managing Storage entities in the ShelveIt application.
 * This class provides functionality to create, retrieve, update, and delete storage locations,
 * as well as manage the hierarchical relationships between storages and their items.
 *
 * The storage hierarchy follows specific rules governed by the StorageType strategy pattern:
 * - RESIDENCE is a top-level storage and cannot have a parent
 * - Other storage types (ROOM, FURNITURE, UNIT) require parent storages
 * - Parent-child relationships are validated based on compatibility rules (according to StorageType strategies)
 */
@Service
@Slf4j
public class DefaultStorageService implements StorageService {

    private final StorageRepository storageRepo;
    private final StorageMapper storageMapper;
    private final ItemMapper itemMapper;

    public DefaultStorageService(StorageRepository storageRepository,
                                 StorageMapper storageMapper,
                                 ItemMapper itemMapper) {
        this.storageRepo = storageRepository;
        this.storageMapper = storageMapper;
        this.itemMapper = itemMapper;
    }

    /**
     * Creates a new storage location.
     *
     * @param storageCreateDTO The DTO containing storage details
     * @return The created storage as a DTO
     * @throws IllegalArgumentException if parent-child relationship rules are violated
     * @throws StorageNotFoundException if the specified parent storage doesn't exist
     */
    @Override
    @Transactional
    public StorageDTO create(StorageCreateDTO storageCreateDTO) {
        log.debug("Creating new storage: {}", storageCreateDTO.getName());
        Storage newStorage = buildStorageFromDTO(storageCreateDTO);
        Storage storage = storageRepo.save(newStorage);
        log.info("Created new storage with ID: {}", storage.getId());
        return storageMapper.toDTO(storage);
    }

    /**
     * Retrieves a storage by its ID.
     *
     * @param storageId ID of the storage to retrieve
     * @return The storage as a DTO
     * @throws StorageNotFoundException if no storage with the given ID exists
     */
    @Override
    @Transactional(readOnly = true)
    public StorageDTO getById(Long storageId) {
        log.debug("Fetching storage with ID: {}", storageId);
        return storageMapper.toDTO(findStorageByIdOrThrow(storageId));
    }

    /**
     * Deletes a storage by its ID.
     *
     * @param storageId ID of the storage to delete
     * @throws StorageNotFoundException if no storage with the given ID exists
     */
    @Override
    @Transactional
    public void delete(Long storageId) {
        log.debug("Deleting storage with ID: {}", storageId);
        findStorageByIdOrThrow(storageId);
        storageRepo.deleteById(storageId);
        log.info("Deleted storage with ID: {}", storageId);
    }

    /**
     * Retrieves all storages in the system.
     *
     * @return List of all storages as DTOs
     */
    @Override
    @Transactional(readOnly = true)
    public List<StorageDTO> getAll() {
        log.debug("Fetching all storages");
        List<Storage> storages = storageRepo.findAll();
        return storageMapper.toDTOList(storages);
    }

    /**
     * Retrieves all items contained in a storage and all its sub-storages recursively.
     *
     * @param storageId ID of the storage to retrieve items from
     * @return List of all items as DTOs
     * @throws StorageNotFoundException if no storage with the given ID exists
     */
    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getAllItemDTOs(Long storageId) {
        log.debug("Fetching all items for storage with ID: {}", storageId);
        return itemMapper.toDTOList(getAllItems(storageId));
    }

    /**
     * Retrieves all direct sub-storages of a parent storage.
     *
     * @param parentId ID of the parent storage
     * @return List of direct child storages as DTOs
     * @throws StorageNotFoundException if no storage with the given parent ID exists
     */
    @Override
    @Transactional(readOnly = true)
    public List<StorageDTO> getSubStorages(Long parentId) {
        log.debug("Fetching sub-storages for parent with ID: {}", parentId);
        findStorageByIdOrThrow(parentId);
        List<Storage> subStorages = storageRepo.findByParentId(parentId);
        return storageMapper.toDTOList(subStorages);
    }

    /**
     * Deletes a storage and moves all its contents (items and sub-storages) to another storage.
     * If the storage to delete is of type RESIDENCE, a target storage must be specified.
     * For other storage types, contents will be moved to the parent storage if no target is specified.
     *
     * @param storageId ID of the storage to delete
     * @param targetStorageId ID of the storage to receive contents (optional for non-RESIDENCE storages)
     * @throws StorageNotFoundException if no storage with the given IDs exists
     * @throws IllegalArgumentException if validation rules (for storage types) are violated
     */
    @Override
    @Transactional
    public void deleteAndMoveContents(Long storageId, Long targetStorageId) {
        log.debug("Starting operation to delete storage ID: {} and move contents to target ID: {}",
                storageId, targetStorageId);
        requireStorageIdNotNull(storageId);
        Storage storageToDelete = findStorageByIdOrThrow(storageId);
        if (storageToDelete.getType() == StorageType.RESIDENCE) {
            validateTargetForResidence(targetStorageId, storageToDelete);
        }
        Storage targetStorage = getTargetStorage(targetStorageId, storageToDelete.getParent());

        moveItems(storageToDelete, targetStorage);
        moveSubStorages(storageToDelete, targetStorage);

        storageRepo.save(targetStorage);
        storageRepo.delete(storageToDelete);
        log.info("Storage ID: {} deleted and contents moved to target ID: {}", storageId, targetStorage.getId());
    }

    /**
     * Updates the basic properties of a storage (name and/or type).
     * Type changes are validated against the storage hierarchy rules.
     *
     * @param id ID of the storage to update
     * @param updateDTO DTO containing the properties to update
     * @return The updated storage as a DTO
     * @throws StorageNotFoundException if no storage with the given ID exists
     * @throws IllegalArgumentException if type change violates hierarchy rules
     */
    @Override
    @Transactional
    public StorageDTO updateProperties(Long id, StorageUpdateDTO updateDTO) {
        log.debug("Updating properties for storage ID: {}", id);
        Storage storage = findStorageByIdOrThrow(id);
        Storage updatedStorage = applyChanges(storage, updateDTO);
        Storage savedUpdatedStorage = storageRepo.save(updatedStorage);
        log.info("Updated properties for storage ID: {}", id);
        return storageMapper.toDTO(savedUpdatedStorage);
    }

    private Storage findStorageByIdOrThrow(Long storageId) {
        return storageRepo.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));
    }

    private Storage getParentStorage(Long parentId) {
        return (parentId != null) ? findStorageByIdOrThrow(parentId) : null;
    }

    private Storage getTargetStorage(Long targetStorageId, Storage parentStorage) {
        if (targetStorageId != null) {
            requireCurrAndTargetStorageIdsNotEqual(parentStorage.getId(), targetStorageId);
            return findStorageByIdOrThrow(targetStorageId);
        }
        return parentStorage;
    }

    private void requireStorageIdNotNull(Long storageId) {
        if (storageId == null) {
            throw new IllegalArgumentException("Storage ID cannot be null");
        }
    }

    private void validateTargetForResidence(Long targetStorageId, Storage storageToDelete) {
        try {
            requireStorageIdNotNull(targetStorageId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No target storage provided. " +
                    "Storage can't be deleted without moving its contents.");
        }
    }

    private void requireCurrAndTargetStorageIdsNotEqual(Long storageId, Long targetStorageId) {
        if (storageId.equals(targetStorageId)) {
            throw new IllegalArgumentException("Cannot move contents to the same storage that is being deleted");
        }
    }

    private Storage buildStorageFromDTO(StorageCreateDTO storageCreateDTO) {
        Storage parent = getParentStorage(storageCreateDTO.getParentId());
        validateStorageParentRequirement(storageCreateDTO.getType(), parent);
        if (parent != null) {
            validateParentCanContainType(parent, storageCreateDTO.getType());
        }
        return createNewStorage(storageCreateDTO, parent);
    }

    private Storage createNewStorage(StorageCreateDTO storageCreateDTO, Storage parent) {
        Storage newStorage = storageMapper.toEntity(storageCreateDTO);
        newStorage.setParent(parent);
        return newStorage;
    }

    private Storage applyChanges(Storage storage, StorageUpdateDTO updateDTO) {
        updateNameIfPresent(storage, updateDTO.getName());
        updateTypeIfPresent(storage, updateDTO.getType());
        return storage;
    }

    private void updateTypeIfPresent(Storage storage, StorageType type) {
        if (type != null) {
            validateTypeUpdate(storage, type);
            storage.setType(type);
        }
    }

    private void updateNameIfPresent(Storage storage, String name) {
        if (name != null && !name.trim().isEmpty()) {
            storage.setName(name);
        }
    }

    private void validateTypeUpdate(Storage storage, StorageType newType) {
        validateStorageParentRequirement(newType, storage.getParent());
        if (storage.getParent() != null) {
            validateParentCanContainType(storage.getParent(), newType);
        }
        validateNewTypeCanContainExistingSubstorages(storage, newType);
    }

    private void validateParentCanContainType(Storage parent, StorageType childType) {
        if (!parent.getType().getStrategy().canContain(childType)) {
            throw new IllegalArgumentException(
                    parent.getType() + " cannot contain " + childType);
        }
    }

    private void validateStorageParentRequirement(StorageType type, Storage parent) {
        boolean hasParent = parent != null;
        if (!type.getStrategy().canHaveParent() && hasParent) {
            throw new IllegalArgumentException(type + " storage should not have a parent.");
        }
        if (type.getStrategy().canHaveParent() && !hasParent) {
            throw new IllegalArgumentException(type + " storage requires a parent.");
        }
    }

    private void validateNewTypeCanContainExistingSubstorages(Storage storage, StorageType newType) {
        for (Storage subStorage : storage.getSubStorages()) {
            if (!newType.getStrategy().canContain(subStorage.getType())) {
                throw new IllegalArgumentException(
                        newType + " cannot contain existing sub-storage of type " + subStorage.getType());
            }
        }
    }

    private List<Item> getAllItems(Long storageId) {
        Storage storage = findStorageByIdOrThrow(storageId);
        List<Item> allItems = new ArrayList<>();
        collectItemsRecursively(storage, allItems);
        return allItems;
    }

    private void collectItemsRecursively(Storage storage, List<Item> itemsCollection) {
        itemsCollection.addAll(storage.getItems());
        for (Storage subStorage : storage.getSubStorages()) {
            collectItemsRecursively(subStorage, itemsCollection);
        }
    }

    private void moveItems(Storage from, Storage to) {
        List<Item> itemsToMove = new ArrayList<>(from.getItems());
        for (Item item : itemsToMove) {
            item.setStorage(to);
            to.getItems().add(item);
        }
        from.getItems().clear();
    }

    private void moveSubStorages(Storage from, Storage to) {
        List<Storage> subStoragesToMove = new ArrayList<>(from.getSubStorages());
        for (Storage subStorage : subStoragesToMove) {
            subStorage.setParent(to);
            to.getSubStorages().add(subStorage);
        }
        from.getSubStorages().clear();
    }
}
