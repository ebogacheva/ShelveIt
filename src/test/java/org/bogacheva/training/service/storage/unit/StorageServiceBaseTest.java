package org.bogacheva.training.service.storage.unit;

import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.dto.StorageUpdateDTO;
import org.bogacheva.training.service.mapper.ItemMapper;
import org.bogacheva.training.service.mapper.StorageMapper;
import org.bogacheva.training.service.storage.DefaultStorageService;
import org.bogacheva.training.service.storage.StorageValidatorService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public abstract class StorageServiceBaseTest {

    @Mock
    protected StorageMapper storageMapper;

    @Mock
    protected ItemMapper itemMapper;

    @Mock
    protected StorageRepository storageRepo;

    @Mock
    protected StorageValidatorService validator;

    @InjectMocks
    protected DefaultStorageService storageService;

    protected Storage createStorage(Long id, String name, StorageType type, Storage parent) {
        Storage storage = new Storage(name, type, parent);
        storage.setId(id);
        return storage;
    }

    protected Storage createStorageWithItems(Long id, String name, StorageType type,
                                             Storage parent, List<Item> items) {
        Storage storage = createStorage(id, name, type, parent);
        storage.setItems(items);
        return storage;
    }

    protected Storage createStorageWithSubStorages(Long id, String name, StorageType type,
                                                   Storage parent, List<Storage> subStorages) {
        Storage storage = createStorage(id, name, type, parent);
        storage.setSubStorages(subStorages);
        return storage;
    }

    protected StorageDTO createStorageDTO(Long id, String name, StorageType type, Long parentId) {
        return new StorageDTO(id, name, type, new ArrayList<>(), new ArrayList<>(), parentId);
    }

    protected StorageCreateDTO createStorageCreateDTO(String name, StorageType type, Long parentId) {
        return new StorageCreateDTO(name, type, parentId);
    }

    protected StorageUpdateDTO createStorageUpdateDTO(String name, StorageType type) {
        StorageUpdateDTO dto = new StorageUpdateDTO();
        dto.setName(name);
        dto.setType(type);
        return dto;
    }

    protected Item createItem(Long id, String name, Storage storage) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setStorage(storage);
        return item;
    }

    protected ItemDTO createItemDTO(Long id, String name, StorageDTO storage) {
        return ItemDTO.builder()
                .id(id)
                .name(name)
                .storage(storage)
                .build();
    }
}

