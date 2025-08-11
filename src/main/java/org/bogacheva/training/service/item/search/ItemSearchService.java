package org.bogacheva.training.service.item.search;

import org.bogacheva.training.service.dto.ItemDTO;

import java.util.List;

public interface ItemSearchService {

    /**
     * Search items by partial, case-insensitive name match.
     */
    List<ItemDTO> searchItemsByName(String partialName);

    /**
     * Search items by keywords (case-insensitive partial match).
     * Accepts multiple keywords, any matching keyword returns the item.
     */
    List<ItemDTO> searchItemsByKeywords(List<String> keywords);

    /**
     * Search items by storage name (partial, case-insensitive).
     * Retrieves all items in storages matching the name, including sub-storages recursively.
     */
    List<ItemDTO> searchItemsByStorageName(String partialStorageName);

    /**
     * Get items in the same storage as the given item, excluding the item itself.
     */
    List<ItemDTO> getItemsNear(Long itemId);

    /**
     * Get all items in a given storage.
     */
    List<ItemDTO> getByStorageId(Long storageId);

    /**
     * Get IDs of all storages in hierarchy containing the item.
     */
    List<Long> getStorageHierarchyIds(Long itemId);
}
