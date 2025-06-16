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
import java.util.function.Consumer;

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
    private final StorageValidatorService validator;

    public DefaultStorageService(StorageRepository storageRepository,
                                 StorageMapper storageMapper,
                                 ItemMapper itemMapper,
                                 StorageValidatorService validator) {
        this.storageRepo = storageRepository;
        this.storageMapper = storageMapper;
        this.itemMapper = itemMapper;
        this.validator = validator;
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
        validator.validateStorageCreation(storageCreateDTO);
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
        Storage storage = findStorageByIdOrThrow(storageId);
        return storageMapper.toDTO(storage);
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
        List<Item> items = getAllItems(storageId);
        return itemMapper.toDTOList(items);
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
        validator.validateNotNull(storageId, () -> new IllegalArgumentException("Storage ID cannot be null"));
        Storage storageToDelete = findStorageByIdOrThrow(storageId);
        Storage targetStorage = getTargetStorage(storageToDelete, targetStorageId);

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
     * @param dto DTO containing the properties to update
     * @return The updated storage as a DTO
     * @throws StorageNotFoundException if no storage with the given ID exists
     * @throws IllegalArgumentException if type change violates hierarchy rules
     */
    @Override
    @Transactional
    public StorageDTO updateProperties(Long id, StorageUpdateDTO dto) {
        log.debug("Updating properties for storage ID: {}", id);
        Storage storage = findStorageByIdOrThrow(id);
        updateStorageProperties(storage, dto);
        Storage savedUpdatedStorage = storageRepo.save(storage);
        log.info("Updated properties for storage ID: {}", id);
        return storageMapper.toDTO(savedUpdatedStorage);
    }

    private Storage findStorageByIdOrThrow(Long id) {
        return storageRepo.findById(id)
                .orElseThrow(() -> new StorageNotFoundException(id));
    }

    private Storage getTargetStorage(Storage storageToDelete, Long targetStorageId) {
        boolean isResidence = storageToDelete.getType() == StorageType.RESIDENCE;
        if (isResidence && targetStorageId == null) {
            throw new IllegalArgumentException("RESIDENCE storage must specify a target storage for contents");
        }
        if (targetStorageId != null) {
            return findStorageByIdOrThrow(targetStorageId);
        }
        return storageToDelete.getParent();
    }

    private Storage buildStorageFromDTO(StorageCreateDTO dto) {
        Storage parent = dto.getParentId() != null
                ? findStorageByIdOrThrow(dto.getParentId())
                : null;
        if (parent != null) {
            validator.validateHierarchySubStorageRules(parent.getType(), dto.getType());
        }
        Storage newStorage = storageMapper.toEntity(dto);
        newStorage.setParent(parent);
        return newStorage;
    }

    private void updateStorageProperties(Storage storage, StorageUpdateDTO dto) {
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            storage.setName(dto.getName());
        }
        if (dto.getType() != null) {
            validator.validateTypeUpdate(storage, dto.getType());
            storage.setType(dto.getType());
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
        moveElements(
                from.getItems(),
                to.getItems(),
                item -> item.setStorage(to)
        );
    }

    private void moveSubStorages(Storage from, Storage to) {
        moveElements(
                from.getSubStorages(),
                to.getSubStorages(),
                subStorage -> subStorage.setParent(to)
        );
    }

    private <T> void moveElements(List<T> fromList, List<T> toList, Consumer<T> reassignParent) {

        List<T> elementsToMove = new ArrayList<>(fromList);
        for (T element : elementsToMove) {
            reassignParent.accept(element);
            toList.add(element);
        }
        fromList.clear();
    }
}
