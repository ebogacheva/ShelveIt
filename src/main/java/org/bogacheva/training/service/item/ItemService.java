package org.bogacheva.training.service.item;

import jakarta.transaction.Transactional;
import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.repository.item.ItemRepository;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.mapper.ItemMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepo;
    private final StorageRepository storageRepo;
    private final ItemMapper itemMapper;

    public ItemService(ItemRepository itemRepo, StorageRepository storageRepo, ItemMapper itemMapper) {
        this.itemRepo = itemRepo;
        this.storageRepo = storageRepo;
        this.itemMapper = itemMapper;
    }

    public ItemDTO create(ItemCreateDTO itemCreateDTO) {
        Item newItem = itemMapper.toEntity(itemCreateDTO);
        Storage storage = getStorage(itemCreateDTO.getStorageId());
        newItem.setStorage(storage);
        Item item = itemRepo.save(newItem);
        storage.getItems().add(item);
        storageRepo.save(storage);
        return itemMapper.toDTO(item);
    }

    public ItemDTO getById(Long itemId) {
        Item item = itemRepo.findById(itemId).orElseThrow(() -> new NoSuchElementException("Item was not found."));
        return itemMapper.toDTO(item);
    }

    public List<ItemDTO> getAll() {
        return itemMapper.toDTOList(itemRepo.findAll());
    }

    public void delete(Long itemId) {
        itemRepo.deleteById(itemId);
    }

    public List<ItemDTO> getByStorageId(Long storageId) {
        validateStorageExists(storageId);
        List<Item> items = itemRepo.findItemsByStorageId(storageId);
        return itemMapper.toDTOList(items);
    }

    private Storage getStorage(Long storageId) {
        return storageRepo.findById(storageId)
                .orElseThrow(() -> new NoSuchElementException("Storage was not found."));
    }

    private void validateStorageExists(Long storageId) {
        getStorage(storageId);
    }

}
