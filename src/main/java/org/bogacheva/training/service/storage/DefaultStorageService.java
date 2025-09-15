package org.bogacheva.training.service.storage;

import lombok.extern.slf4j.Slf4j;
import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.repository.item.ItemRepository;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.dto.StorageUpdateDTO;
import org.bogacheva.training.exceptions.StorageNotFoundException;
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
    private final ItemRepository itemRepo;
    private final StorageValidatorService validator;

    public DefaultStorageService(StorageRepository storageRepository,
                                 StorageMapper storageMapper,
                                 ItemMapper itemMapper,
                                 ItemRepository itemRepository,
                                 StorageValidatorService validator) {
        this.storageRepo = storageRepository;
        this.storageMapper = storageMapper;
        this.itemMapper = itemMapper;
        this.itemRepo = itemRepository;
        this.validator = validator;
    }

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

    @Override
    @Transactional(readOnly = true)
    public StorageDTO getById(Long storageId) {
        log.debug("Fetching storage with ID: {}", storageId);
        Storage storage = findStorageByIdOrThrow(storageId);
        return storageMapper.toDTO(storage);
    }

    /**
     * Retrieves all storages optionally filtered by type.
     *
     * @param type the storage type to filter by, or null to return all
     * @return list of StorageDTOs
     */
    @Override
    @Transactional(readOnly = true)
    public List<StorageDTO> getAll(StorageType type) {
        log.debug("Fetching storages with type: {}", type);
        List<Storage> storages = (type == null)
                ? storageRepo.findAll()
                : storageRepo.findByType(type);
        return storageMapper.toDTOList(storages);
    }

    @Override
    @Transactional
    public StorageDTO update(Long id, StorageUpdateDTO dto) {
        log.debug("Updating for storage with ID: {}", id);
        Storage storage = findStorageByIdOrThrow(id);
        updateStorageProperties(storage, dto);
        Storage savedUpdatedStorage = storageRepo.save(storage);
        log.info("Updated storage with ID: {}", id);
        return storageMapper.toDTO(savedUpdatedStorage);
    }

    @Override
    @Transactional
    public void delete(Long storageId) {
        log.debug("Deleting storage with ID: {}", storageId);
        findStorageByIdOrThrow(storageId);
        storageRepo.deleteById(storageId);
        log.info("Deleted storage with ID: {}", storageId);
    }

    /**
     * Retrieves all items associated with a storage and its sub-storages recursively.
     *
     * @param storageId the storage ID
     * @return list of ItemDTOs
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
     * @param parentId the parent storage ID
     * @return list of StorageDTOs
     * @throws StorageNotFoundException if parent storage does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public List<StorageDTO> getSubStorages(Long parentId) {
        log.debug("Fetching sub-storages for parent with ID: {}", parentId);
        findStorageByIdOrThrow(parentId);
        List<Storage> subStorages = storageRepo.findByParentId(parentId);
        return storageMapper.toDTOList(subStorages);
    }

    @Override
    @Transactional
    public StorageDTO addItems(Long storageId, List<Long> itemIds) {
        log.debug("Adding items {} to storage {}", itemIds, storageId);
        Storage storage = findStorageByIdOrThrow(storageId);
        List<Item> items = itemRepo.findAllById(itemIds);
        items.forEach(item -> item.setStorage(storage));
        storage.getItems().addAll(items);
        Storage updated = storageRepo.save(storage);
        return storageMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public StorageDTO removeItems(Long storageId, List<Long> itemIds) {
        log.debug("Removing items {} from storage {}", itemIds, storageId);
        Storage storage = findStorageByIdOrThrow(storageId);
        storage.getItems().removeIf(item -> {
            if (itemIds.contains(item.getId())) {
                item.setStorage(null);
                return true;
            }
            return false;
        });
        Storage updated = storageRepo.save(storage);
        return storageMapper.toDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StorageDTO> searchByNameAndType(String name, StorageType type) {
        log.debug("Searching storages by name '{}' and type {}", name, type);
        List<Storage> storages = (type == null)
                ? storageRepo.findByNameContainingIgnoreCase(name)
                : storageRepo.findByNameContainingIgnoreCaseAndType(name, type);
        return storageMapper.toDTOList(storages);
    }

    private Storage findStorageByIdOrThrow(Long id) {
        return storageRepo.findById(id)
                .orElseThrow(() -> new StorageNotFoundException(id));
    }

//    private Storage getTargetStorage(Storage storageToDelete, Long targetStorageId) {
//        boolean isResidence = storageToDelete.getType() == StorageType.RESIDENCE;
//        if (isResidence && targetStorageId == null) {
//            throw new IllegalArgumentException("RESIDENCE storage must specify a target storage for contents");
//        }
//        if (targetStorageId != null) {
//            return findStorageByIdOrThrow(targetStorageId);
//        }
//        return storageToDelete.getParent();
//    }

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

    // TODO: Implement moveContents method to handle moving items and sub-storages
//    private void moveItems(Storage from, Storage to) {
//        moveElements(
//                from.getItems(),
//                to.getItems(),
//                item -> item.setStorage(to)
//        );
//    }
//
//    private void moveSubStorages(Storage from, Storage to) {
//        moveElements(
//                from.getSubStorages(),
//                to.getSubStorages(),
//                subStorage -> subStorage.setParent(to)
//        );
//    }
//
//    private <T> void moveElements(List<T> fromList, List<T> toList, Consumer<T> reassignParent) {
//
//        List<T> elementsToMove = new ArrayList<>(fromList);
//        for (T element : elementsToMove) {
//            reassignParent.accept(element);
//            toList.add(element);
//        }
//        fromList.clear();
//    }
}
