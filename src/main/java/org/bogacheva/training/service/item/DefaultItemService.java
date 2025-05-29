package org.bogacheva.training.service.item;

import jakarta.transaction.Transactional;
import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.repository.item.ItemRepository;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.ItemUpdateDTO;
import org.bogacheva.training.service.mapper.ItemMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class DefaultItemService implements ItemService {

    private static final String ITEM_NOT_FOUND = "Item with ID: %s was not found.";
    private static final String STORAGE_NOT_FOUND = "Storage with ID: %s was not found.";
    
    private final ItemRepository itemRepo;
    private final StorageRepository storageRepo;
    private final ItemMapper itemMapper;

    public DefaultItemService(ItemRepository itemRepo, StorageRepository storageRepo, ItemMapper itemMapper) {
        this.itemRepo = itemRepo;
        this.storageRepo = storageRepo;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDTO create(ItemCreateDTO itemCreateDTO) {
        Item newItem = itemMapper.toEntity(itemCreateDTO);
        Storage storage = getStorageById(itemCreateDTO.getStorageId());
        newItem.setStorage(storage);
        Item item = itemRepo.save(newItem);
        storage.getItems().add(item);
        storageRepo.save(storage);
        return itemMapper.toDTO(item);
    }

    @Override
    public ItemDTO getById(Long itemId) {
        Item item = getItemById(itemId);
        return itemMapper.toDTO(item);
    }

    @Override
    public List<ItemDTO> getAll() {
        return itemMapper.toDTOList(itemRepo.findAll());
    }

    @Override
    public void delete(Long itemId) {
        itemRepo.deleteById(itemId);
    }

    @Override
    public List<ItemDTO> getByStorageId(Long storageId) {
        validateStorageExists(storageId);
        List<Item> items = itemRepo.findItemsByStorageId(storageId);
        return itemMapper.toDTOList(items);
    }

    @Override
    public List<ItemDTO> findByKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return getAll();
        }
        List<Item> items = itemRepo.findByAnyKeyword(keywords);
        return itemMapper.toDTOList(items);
    }

    @Override
    public ItemDTO update(Long itemId, ItemUpdateDTO itemUpdateDTO) {
        validateItemUpdateDTO(itemUpdateDTO);
        Item item = getItemById(itemId);
        applyChanges(item, itemUpdateDTO);
        Item savedItem = itemRepo.save(item);
        return itemMapper.toDTO(savedItem);
    }

    private void applyChanges(Item item, ItemUpdateDTO itemUpdateDTO) {
        updateNameIfPresent(item, itemUpdateDTO.getName());
        updateKeywordsIfPresent(item, itemUpdateDTO.getKeywords());
        updateStorageIfChanged(item, itemUpdateDTO.getStorageId());
    }

    private void validateItemUpdateDTO(ItemUpdateDTO itemUpdateDTO) {
        if (itemUpdateDTO == null) {
            throw new IllegalArgumentException("ItemUpdateDTO must not be null.");
        }
    }

    private Item getItemById(Long itemId) {
        return itemRepo.findById(itemId).orElseThrow(() ->
                new NoSuchElementException(formatMsg(ITEM_NOT_FOUND, itemId)));
    }

    private void updateNameIfPresent(Item item, String name) {
        if (name != null) {
            item.setName(name);
        }
    }

    private void updateKeywordsIfPresent(Item item, List<String> keywords) {
        if (keywords != null) {
            item.setKeywords(keywords);
        }
    }

    private void updateStorageIfChanged(Item item, Long newStorageId) {
        if (shouldUpdateStorage(item, newStorageId)) {
            Storage newStorage = getStorageById(newStorageId);
            item.setStorage(newStorage);
        }
    }

    private boolean shouldUpdateStorage(Item item, Long newStorageId) {
        return newStorageId != null &&
                isStorageIdChanged(item.getStorage().getId(), newStorageId);
    }

    private boolean isStorageIdChanged(Long currStorageId, Long newStorageId) {
        return !currStorageId.equals(newStorageId);
    }

    private Storage getStorageById(Long storageId) {
        return storageRepo.findById(storageId)
                .orElseThrow(() -> new NoSuchElementException(
                        formatMsg(STORAGE_NOT_FOUND, storageId))
                );
    }

    private void validateStorageExists(Long storageId) {
        getStorageById(storageId);
    }

    private String formatMsg(String baseMessage, Long id) {
        return String.format(baseMessage, id);
    }
}
