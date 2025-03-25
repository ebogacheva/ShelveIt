package org.bogacheva.training.service.item;

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
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ItemService {

    private static final AtomicLong counter = new AtomicLong(1);

    private final ItemRepository itemRepo;
    private final StorageRepository storageRepo;
    private final ItemMapper itemMapper;

    public ItemService(ItemRepository itemRepo, StorageRepository storageRepo, ItemMapper itemMapper) {
        this.itemRepo = itemRepo;
        this.storageRepo = storageRepo;
        this.itemMapper = itemMapper;
    }

    private static long getNextId() {
        return counter.getAndIncrement();
    }

    public void create(ItemCreateDTO itemCreateDTO) {
        Item item = itemMapper.toEntity(itemCreateDTO);
        Storage storage = getStorage(itemCreateDTO.getStorageId());
        item.setStorage(storage);
        item.setId(getNextId());
        itemRepo.add(item);
        storage.getItems().add(item);
    }

    public ItemDTO getById(Long itemId) {
        return itemRepo.getById(itemId).map(itemMapper::toDTO).orElseThrow(() -> new NoSuchElementException("Item was not found."));
    }

    public List<ItemDTO> getAll() {
        return itemMapper.toDTOList(itemRepo.getAll());
    }

    public void delete(Long itemId) {
        itemRepo.remove(itemId);
    }

    private Storage getStorage(Long storageId) {
        return storageRepo.getById(storageId)
                .orElseThrow(() -> new NoSuchElementException("Storage was not found."));
    }

}
