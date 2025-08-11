package org.bogacheva.training.service.item.crud;

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
import java.util.Objects;
import java.util.stream.Collectors;

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
        normalizeKeywords(newItem);
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
    @Transactional
    public ItemDTO update(Long itemId, ItemUpdateDTO itemUpdateDTO) {
        validateItemUpdateDTO(itemUpdateDTO);
        Item item = getItemByIdOrThrow(itemId);
        applyChanges(item, itemUpdateDTO);
        normalizeKeywords(item);
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
        boolean hasName = itemUpdateDTO.getName() != null;
        boolean hasKeywords = itemUpdateDTO.getKeywords() != null;
        boolean hasStorageId = itemUpdateDTO.getStorageId() != null;

        if (!hasName && !hasKeywords && !hasStorageId) {
            throw new IllegalArgumentException("At least one field must be provided for update.");
        }
        if (itemUpdateDTO.getName() != null && itemUpdateDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name, if provided, cannot be empty.");
        }
    }

    private Item applyChanges(Item item, ItemUpdateDTO itemUpdateDTO) {
        if (itemUpdateDTO.getName() != null) item.setName(itemUpdateDTO.getName());
        if (itemUpdateDTO.getKeywords() != null) item.setKeywords(new ArrayList<>(itemUpdateDTO.getKeywords()));
        if (itemUpdateDTO.getStorageId() != null && !itemUpdateDTO.getStorageId().equals(item.getStorage().getId())) {
            item.setStorage(getStorageByIdOrThrow(itemUpdateDTO.getStorageId()));
        }
        return item;
    }

    private void normalizeKeywords(Item item) {
        List<String> keywords = item.getKeywords();
        if (keywords != null) {
            List<String> normalized = keywords.stream()
                    .filter(Objects::nonNull)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            item.setKeywords(normalized);
        }
    }
}
