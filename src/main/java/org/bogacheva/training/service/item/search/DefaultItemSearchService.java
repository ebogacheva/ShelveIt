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

import java.util.*;
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

    @Override
    public List<ItemDTO> search(String partialName, List<String> keywords) {
       if (!hasName(partialName) && !hasKeywords(keywords)) {
           return Collections.emptyList();
       }
       Set<Item> results = new HashSet<>();
       if (hasName(partialName)) {
           results.addAll(searchItemsByName(partialName));
       }
       if (hasKeywords(keywords)) {
           results.addAll(searchItemsByKeywords(keywords));
       }
       return itemMapper.toDTOList(new ArrayList<>(results));
    }

    @Override
    public List<ItemDTO> searchItemsByStorageName(String partialStorageName) {
        if (!hasName(partialStorageName)) {
            return Collections.emptyList();
        }
        List<Storage> storages = searchStoragesByName(partialStorageName);
        List<Item> items = new ArrayList<>();
        for (Storage storage : storages) {
            items.addAll(storage.getItems());
        }
        return itemMapper.toDTOList(items);
    }

    @Override
    public List<ItemDTO> getItemsNear(Long itemId) {
        Item item = getItemByIdOrThrow(itemId);
        Storage storage = getStorageOrThrow(item);
        List<Item> itemsNear = itemRepository.findItemsByStorageIdAndExcludeItemId(storage.getId(), itemId);
        return itemMapper.toDTOList(itemsNear);
    }

    @Override
    public List<ItemDTO> getByStorageId(Long storageId) {
        getStorageByIdOrThrow(storageId);
        return itemMapper.toDTOList(itemRepository.findItemsByStorageId(storageId));
    }

    @Override
    public List<Long> getStorageHierarchyIds(Long itemId) {
        Item item = getItemByIdOrThrow(itemId);
        getStorageOrThrow(item);
        return itemRepository.findStorageHierarchyIds(itemId);
    }

    private String getLikePattern(String partialName) {
        return "%" + partialName.toLowerCase() + "%";
    }

    private List<Storage> searchStoragesByName(String partialName) {
        return storageRepository.findByNameLikeIgnoreCase(getLikePattern(partialName));
    }

    private List<Item> searchItemsByName(String partialName) {
        return itemRepository.findByNameLikeIgnoreCase(getLikePattern(partialName));
    }

    private List<Item> searchItemsByKeywords(List<String> keywords) {
        List<String> lowerKeywords = keywords.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        return itemRepository.findByAnyKeyword(lowerKeywords);
    }

    private Item getItemByIdOrThrow(Long itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    private void getStorageByIdOrThrow(Long storageId) {
        if (storageId == null) {
            throw new IllegalArgumentException("Storage ID cannot be null");
        }
        storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));
    }

    private Storage getStorageOrThrow(Item item) {
        Storage storage = item.getStorage();
        if (storage == null) {
            throw new InvalidItemOperationException(String.format(ITEM_HAS_NO_STORAGE, item.getId()));
        }
        return storage;
    }

    private boolean hasName(String partialName) {
        return partialName != null && !partialName.trim().isEmpty();
    }

    private boolean hasKeywords(List<String> keywords) {
        return keywords != null && !keywords.isEmpty();
    }
}
