package org.bogacheva.training.service.item.search;

import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.repository.item.ItemRepository;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.exceptions.InvalidItemOperationException;
import org.bogacheva.training.service.exceptions.ItemNotFoundException;
import org.bogacheva.training.service.exceptions.StorageNotFoundException;
import org.bogacheva.training.service.mapper.ItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DefaultItemSearchService implements ItemSearchService {

    private static final String ITEM_HAS_NO_STORAGE = "Item with ID: %s has no associated storage.";

    private final ItemRepository itemRepository;
    private final StorageRepository storageRepository;
    private final ItemMapper itemMapper;

    public DefaultItemSearchService(ItemRepository itemRepository,
                                    StorageRepository storageRepository,
                                    ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.storageRepository = storageRepository;
        this.itemMapper = itemMapper;
    }

    /**
     * Search items by partial, case-insensitive name match.
     */
    @Override
    public List<ItemDTO> searchItemsByName(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String pattern = "%" + partialName.toLowerCase() + "%";
        List<Item> items = itemRepository.findByNameLikeIgnoreCase(pattern);
        return itemMapper.toDTOList(items);
    }

    /**
     * Search items by keywords (case-insensitive partial match).
     * Accepts multiple keywords, any matching keyword returns the item.
     */
    @Override
    public List<ItemDTO> searchItemsByKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> lowerKeywords = keywords.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findByAnyKeyword(lowerKeywords);
        return itemMapper.toDTOList(items);
    }

    /**
     * Search items by storage name (partial, case-insensitive).
     * Retrieves all items in storages matching the name, including sub-storages recursively.
     */
    @Override
    public List<ItemDTO> searchItemsByStorageName(String partialStorageName) {
        if (partialStorageName == null || partialStorageName.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String pattern = "%" + partialStorageName.toLowerCase() + "%";

        List<Storage> matchedStorages = storageRepository.findByNameLikeIgnoreCase(pattern);
        List<Item> allItems = new ArrayList<>();
        for (Storage storage : matchedStorages) {
            collectItemsRecursively(storage, allItems);
        }
        return itemMapper.toDTOList(allItems);
    }

    @Override
    public List<ItemDTO> getItemsNear(Long itemId) {
        Item item = getItemByIdOrThrow(itemId);
        Storage storage = item.getStorage();
        validateNonNullStorage(itemId, storage);
        List<Item> itemsNear = itemRepository.findItemsByStorageIdAndExcludeItemId(storage.getId(), itemId);
        return itemMapper.toDTOList(itemsNear);
    }

    @Override
    public List<ItemDTO> getByStorageId(Long storageId) {
        getStorageByIdOrThrow(storageId);
        List<Item> items = itemRepository.findItemsByStorageId(storageId);
        return itemMapper.toDTOList(items);
    }

    @Override
    public List<Long> getStorageHierarchyIds(Long itemId) {
        Item item = getItemByIdOrThrow(itemId);
        Storage storage = item.getStorage();
        validateNonNullStorage(itemId, storage);
        return itemRepository.findStorageHierarchyIds(itemId);
    }

    private void collectItemsRecursively(Storage storage, List<Item> itemsCollection) {
        itemsCollection.addAll(storage.getItems());
        for (Storage subStorage : storage.getSubStorages()) {
            collectItemsRecursively(subStorage, itemsCollection);
        }
    }

    private Item getItemByIdOrThrow(Long itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    private Storage getStorageByIdOrThrow(Long storageId) {
        if (storageId == null) {
            throw new IllegalArgumentException("Storage ID cannot be null");
        }
        return storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));
    }

    private void validateNonNullStorage(Long itemId, Storage storage) {
        if (storage == null) {
            throw new InvalidItemOperationException(String.format(ITEM_HAS_NO_STORAGE, itemId));
        }
    }
}
