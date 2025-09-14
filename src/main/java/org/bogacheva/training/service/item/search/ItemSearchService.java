package org.bogacheva.training.service.item.search;

import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.exceptions.InvalidItemOperationException;
import org.bogacheva.training.service.exceptions.ItemNotFoundException;
import org.bogacheva.training.service.exceptions.StorageNotFoundException;

import java.util.List;

public interface ItemSearchService {

    /**
     * Search items by partial name and/or keywords.
     * <p>
     * Both parameters are optional, but at least one should be provided.
     * - {@code partialName}: partial, case-insensitive match on item name.
     * - {@code keywords}: list of keywords; case-insensitive, any matching keyword returns the item.
     *
     * @param partialName partial name to search for (may be null or empty)
     * @param keywords list of keywords to search for (may be null or empty)
     * @return list of items matching the criteria, empty list if both partialName and keywords are null/empty
     */
    List<ItemDTO> search(String partialName, List<String> keywords);

    /**
     * Search items by storage name.
     * <p>
     * Performs partial, case-insensitive match on storage name.
     * Only items in the matching storage(s) are returned; sub-storages are not included.
     *
     * @param partialStorageName partial name of storage
     * @return list of items in matching storages, empty list if {@code partialStorageName} is null or blank
     */
    List<ItemDTO> searchItemsByStorageName(String partialStorageName);

    /**
     * Get all items stored in the same storage as the specified item,
     * excluding the item itself.
     *
     * @param itemId ID of the reference item
     * @return list of other items in the same storage
     * @throws IllegalArgumentException if {@code itemId} is null
     * @throws ItemNotFoundException if item with the given ID does not exist
     * @throws InvalidItemOperationException if the item has no associated storage
     */
    List<ItemDTO> getItemsNear(Long itemId);

    /**
     * Get all items in a specific storage by storage ID.
     *
     * @param storageId ID of the storage
     * @return list of items in the storage
     * @throws IllegalArgumentException if {@code storageId} is null
     * @throws StorageNotFoundException if storage with the given ID does not exist
     */
    List<ItemDTO> getByStorageId(Long storageId);

    /**
     * Get the IDs of all storages in the hierarchy that contain the specified item.
     *
     * @param itemId ID of the item
     * @return list of storage IDs from the item's storage hierarchy
     * @throws IllegalArgumentException if {@code itemId} is null
     * @throws ItemNotFoundException if item with the given ID does not exist
     * @throws InvalidItemOperationException if the item has no associated storage
     */
    List<Long> getStorageHierarchyIds(Long itemId);
}
