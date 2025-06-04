package org.bogacheva.training.service.item;

import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageDTO;

import java.util.ArrayList;
import java.util.List;

public abstract class TestObjectsFactory {

    private static final List<String> DEFAULT_KEYWORDS = List.of("keyword1", "keyword2", "keyword3");

    public static Storage getResidence(Long id, String name) {
        return Storage.builder().id(id)
                .name(name)
                .type(StorageType.RESIDENCE)
                .items(new ArrayList<>())
                .subStorages(new ArrayList<>())
                .parent(null)
                .build();
    }

    public static Storage getResidenceWithItem(Long id, String name, List<Item> items) {
        Storage storage = getResidence(id, name);
        storage.setItems(items);
        return storage;
    }

    public static ItemCreateDTO getItemCreateDTO(String name, Long storageId) {
        return new ItemCreateDTO(name, storageId, DEFAULT_KEYWORDS);
    }

    public static Item getItem(Long id, String name, Storage storage) {
        return Item.builder()
                .id(id)
                .name(name)
                .keywords(DEFAULT_KEYWORDS)
                .storage(storage)
                .build();
    }

    public static ItemDTO getItemDTO(Long id, String name, StorageDTO storageDTO) {
        return ItemDTO.builder()
                .id(id)
                .name(name)
                .storage(storageDTO)
                .keywords(DEFAULT_KEYWORDS)
                .build();
    }



    public static StorageDTO getStorageDTO(Storage storage) {
        return StorageDTO.builder()
                .id(storage.getId())
                .name(storage.getName())
                .type(storage.getType())
                .items(storage.getItems() != null ? storage.getItems().stream().map(Item::getId).toList() : new ArrayList<>())
                .storages(storage.getSubStorages() != null ? storage.getSubStorages().stream().map(Storage::getId).toList() : new ArrayList<>())
                .parentId(storage.getParent() != null ? storage.getParent().getId() : null)
                .build();
    }

}
