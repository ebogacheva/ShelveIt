package org.bogacheva.training.view.cli.execution;

import lombok.RequiredArgsConstructor;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.item.crud.ItemService;
import org.bogacheva.training.service.item.search.ItemSearchService;
import org.bogacheva.training.service.storage.StorageService;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ServiceCaller {
    
    private final ItemService itemService;
    private final StorageService storageService;
    private final ItemSearchService itemSearchService;
    
    // Create operations
    public ItemDTO createItem(ItemCreateDTO createDTO) {
        return itemService.create(createDTO);
    }
    
    public StorageDTO createStorage(StorageCreateDTO createDTO) {
        return storageService.create(createDTO);
    }
    
    // List operations
    public List<ItemDTO> getAllItems() {
        return itemService.getAll();
    }
    
    public List<StorageDTO> getAllStorages() {
        return storageService.getAll(null);
    }
    
    // Delete operations
    public void deleteItem(Long id) {
        itemService.delete(id);
    }
    
    public void deleteStorage(Long id) {
        storageService.delete(id);
    }
    
    // Get by ID operations
    public ItemDTO getItemById(Long id) {
        return itemService.getById(id);
    }
    
    public StorageDTO getStorageById(Long id) {
        return storageService.getById(id);
    }
    
    // Search operations
    public List<ItemDTO> getItemsByStorageId(Long storageId) {
        return itemSearchService.getByStorageId(storageId);
    }
    
    public List<StorageDTO> getSubStorages(Long storageId) {
        return storageService.getSubStorages(storageId);
    }
    
    public List<ItemDTO> searchItems(String name, List<String> keywords) {
        return itemSearchService.search(name, keywords);
    }
    
    public List<StorageDTO> searchStorages(String name, org.bogacheva.training.domain.storage.StorageType type) {
        return storageService.searchByNameAndType(name, type);
    }
    
    public List<ItemDTO> getItemsNear(Long itemId) {
        return itemSearchService.getItemsNear(itemId);
    }
    
    public List<Long> getStorageHierarchyIds(Long itemId) {
        return itemSearchService.getStorageHierarchyIds(itemId);
    }
}
