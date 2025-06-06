package org.bogacheva.training.service.item;

import org.bogacheva.training.service.exceptions.InvalidItemOperationException;
import org.bogacheva.training.service.exceptions.ItemNotFoundException;
import org.bogacheva.training.service.exceptions.StorageNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.repository.item.ItemRepository;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.ItemUpdateDTO;
import org.bogacheva.training.service.mapper.ItemMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultItemService implements ItemService {

    private static final String ITEM_HAS_NO_STORAGE = "Item with ID: %s has no associated storage.";
    
    private final ItemRepository itemRepo;
    private final StorageRepository storageRepo;
    private final ItemMapper itemMapper;

    public DefaultItemService(ItemRepository itemRepo, StorageRepository storageRepo, ItemMapper itemMapper) {
        this.itemRepo = itemRepo;
        this.storageRepo = storageRepo;
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional
    public ItemDTO create(ItemCreateDTO itemCreateDTO) {
        Storage storage = getStorageByIdOrThrow(itemCreateDTO.getStorageId());
        Item newItem = itemMapper.toEntity(itemCreateDTO);
        newItem.setStorage(storage);
        Item savedItem = itemRepo.save(newItem);
        return itemMapper.toDTO(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDTO getById(Long itemId) {
        Item item = getItemByIdOrThrow(itemId);
        return itemMapper.toDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getAll() {
        return itemMapper.toDTOList(itemRepo.findAll());
    }

    @Override
    @Transactional
    public void delete(Long itemId) {
        getItemByIdOrThrow(itemId);
        itemRepo.deleteById(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getByStorageId(Long storageId) {
        getStorageByIdOrThrow(storageId);
        List<Item> items = itemRepo.findItemsByStorageId(storageId);
        return itemMapper.toDTOList(items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> findByKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return getAll();
        }
        List<Item> items = itemRepo.findByAnyKeyword(keywords);
        return itemMapper.toDTOList(items);
    }

    @Override
    @Transactional
    public ItemDTO update(Long itemId, ItemUpdateDTO itemUpdateDTO) {
        validateItemUpdateDTO(itemUpdateDTO);
        Item item = getItemByIdOrThrow(itemId);
        applyChanges(item, itemUpdateDTO);
        Item savedItem = itemRepo.save(item);
        return itemMapper.toDTO(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getItemsNear(Long itemId) {
        Item item = getItemByIdOrThrow(itemId);
        Storage storage = item.getStorage();
        validateNoneNullStorage(itemId, storage);
        List<Item> itemsNear = itemRepo.findItemsByStorageIdAndExcludeItemId(storage.getId(), itemId);
        return itemMapper.toDTOList(itemsNear);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getStorageHierarchyIds(Long itemId) {
        Item item = getItemByIdOrThrow(itemId);
        Storage storage = item.getStorage();
        validateNoneNullStorage(itemId, storage);
        return itemRepo.findStorageHierarchyIds(itemId);
    }

    private Item getItemByIdOrThrow(Long itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        return itemRepo.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    private Storage getStorageByIdOrThrow(Long storageId) {
        if (storageId == null) {
            throw new IllegalArgumentException("Storage ID cannot be null");
        }
        return storageRepo.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));
    }

    private void validateNoneNullStorage(Long itemId, Storage storage) {
        if (storage == null) {
            throw new InvalidItemOperationException(String.format(ITEM_HAS_NO_STORAGE, itemId));
        }
    }

    private void validateItemUpdateDTO(ItemUpdateDTO itemUpdateDTO) {
        if (itemUpdateDTO == null) {
            throw new IllegalArgumentException("ItemUpdateDTO must not be null.");
        }
    }

    private void applyChanges(Item item, ItemUpdateDTO itemUpdateDTO) {
        updateNameIfPresent(item, itemUpdateDTO.getName());
        updateKeywordsIfPresent(item, itemUpdateDTO.getKeywords());
        updateStorageIfChanged(item, itemUpdateDTO.getStorageId());
    }

    private void updateNameIfPresent(Item item, String name) {
        if (name != null && !name.trim().isEmpty()) {
            item.setName(name);
        }
    }

    private void updateKeywordsIfPresent(Item item, List<String> keywords) {
        if (keywords != null) {
            List<String> newKeywords = new ArrayList<>(keywords);
            item.setKeywords(newKeywords);
        }
    }

    private void updateStorageIfChanged(Item item, Long newStorageId) {
        if (shouldUpdateStorage(item, newStorageId)) {
            Storage newStorage = getStorageByIdOrThrow(newStorageId);
            item.setStorage(newStorage);
        }
    }

    private boolean shouldUpdateStorage(Item item, Long newStorageId) {
        validateNoneNullStorage(item.getId(), item.getStorage());
        return newStorageId != null &&
                !item.getStorage().getId().equals(newStorageId);
    }
}
