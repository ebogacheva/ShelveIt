package org.bogacheva.training.service.execution.rest;

import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.domain.storage.StorageType;

import java.util.List;

public interface ServiceCaller {
    ItemDTO createItem(ItemCreateDTO createDTO);
    StorageDTO createStorage(StorageCreateDTO createDTO);
    List<ItemDTO> getAllItems();
    List<StorageDTO> getAllStorages();
    void deleteItem(Long id);
    void deleteStorage(Long id);
    ItemDTO getItemById(Long id);
    StorageDTO getStorageById(Long id);
    List<ItemDTO> getItemsByStorageId(Long storageId);
    List<StorageDTO> getSubStorages(Long storageId);
    List<ItemDTO> searchItems(String name, List<String> keywords);
    List<StorageDTO> searchStorages(String name, StorageType type);
    List<ItemDTO> getItemsNear(Long itemId);
    List<Long> getStorageHierarchyIds(Long itemId);
}

