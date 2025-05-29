package org.bogacheva.training.service.storage;

import jakarta.transaction.Transactional;
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

@Service
@Transactional
public class StorageService {

    private final StorageRepository storageRepo;
    private final StorageMapper storageMapper;
    private final ItemMapper itemMapper;

    public StorageService(StorageRepository storageRepository, StorageMapper storageMapper, ItemMapper itemMapper) {
        this.storageRepo = storageRepository;
        this.storageMapper = storageMapper;
        this.itemMapper = itemMapper;
    }

    public StorageDTO create(StorageCreateDTO storageCreateDTO) {
        Storage newStorage = buildStorageFromDTO(storageCreateDTO);
        Storage storage = storageRepo.save(newStorage);
        return storageMapper.toDTO(storage);
    }

    public StorageDTO getById(Long storageId) {
        return storageMapper.toDTO(getStorageById(storageId));
    }

    public void delete(Long storageId) {
        storageRepo.deleteById(storageId);
    }

    public List<StorageDTO> getAll() {
        return storageMapper.toDTOList(storageRepo.findAll());
    }

    public List<ItemDTO> getAllItemDTOs(Long storageId) {
        return itemMapper.toDTOList(getAllItems(storageId));
    }

    public List<StorageDTO> getSubStorages(Long parentId) {
        validateStorageExists(parentId);
        return storageRepo.findByParentId(parentId).stream()
                .map(storageMapper::toDTO)
                .toList();
    }

    private Storage buildStorageFromDTO(StorageCreateDTO storageCreateDTO) {
        validateStorageParent(storageCreateDTO);
        Storage parent = getParentStorage(storageCreateDTO.getParentId());
        validateParentChildCompatibility(storageCreateDTO.getType(), parent);

        return createNewStorage(storageCreateDTO, parent);
    }

    private Storage createNewStorage(StorageCreateDTO storageCreateDTO, Storage parent) {
        Storage newStorage = storageMapper.toEntity(storageCreateDTO);
        newStorage.setParent(parent);
        return newStorage;
    }

    private void validateParentChildCompatibility(StorageType type, Storage parent) {
        if (parent != null && !parent.getType().getStrategy().canContain(type)) {
            throw new IllegalArgumentException(parent.getType() + " cannot contain " + type);
        }
    }

    private void validateStorageParent(StorageCreateDTO storageCreateDTO) {
        StorageType type = storageCreateDTO.getType();
        boolean hasParent = storageCreateDTO.getParentId() != null;
        if (!type.getStrategy().canHaveParent() && hasParent) {
            throw new IllegalArgumentException(type + " storage should not have a parent.");
        }
        if (type.getStrategy().canHaveParent() && !hasParent) {
            throw new IllegalArgumentException(type + " storage requires a parent.");
        }
    }

    private Storage getStorageById(Long storageId) {
        return storageRepo.findById(storageId)
                .orElseThrow(() -> new NoSuchElementException("Storage with id " + storageId + " haven't been found."));
    }

    private void validateStorageExists(Long storageId) {
        getStorageById(storageId);
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

    public void deleteAndMoveContents(Long storageId, Long targetStorageId) {
        Storage storageToDelete = getStorageById(storageId);
        if (storageToDelete.getType() == StorageType.RESIDENCE) {
            requireNonNullTargetStorageId(targetStorageId);
        }
        Storage targetStorage = getTargetStorage(targetStorageId, storageToDelete.getParent());

        moveItems(storageToDelete, targetStorage);
        moveSubStorages(storageToDelete, targetStorage);

        storageRepo.save(targetStorage);
        storageRepo.delete(storageToDelete);
    }

    private void moveItems(Storage from, Storage to) {
        for (Item item : from.getItems()) {
            item.setStorage(to);
            to.getItems().add(item);
        }
        from.getItems().clear();
    }

    private void moveSubStorages(Storage from, Storage to) {
        for (Storage subStorage : from.getSubStorages()) {
            subStorage.setParent(to);
            to.getSubStorages().add(subStorage);
        }
        from.getSubStorages().clear();
    }

    private Storage getTargetStorage(Long targetStorageId, Storage parentStorage) {
        return (targetStorageId != null) ? getStorageById(targetStorageId) : parentStorage;
    }

    private void requireNonNullTargetStorageId(Long targetStorageId) {
        if (targetStorageId == null) {
            throw new IllegalArgumentException("No target storage provided. " +
                    "Storage can't be deleted without moving its contents.");
        }
    }
}
