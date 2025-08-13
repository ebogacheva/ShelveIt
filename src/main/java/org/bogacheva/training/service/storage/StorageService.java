package org.bogacheva.training.service.storage;

import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.dto.StorageUpdateDTO;
import org.bogacheva.training.service.exceptions.InvalidStorageHierarchyException;
import org.bogacheva.training.service.exceptions.StorageNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StorageService {

    /**
     * Creates a new storage location.
     *
     * @param storageCreateDTO The DTO containing storage details
     * @return The created storage as a DTO
     * @throws InvalidStorageHierarchyException if parent-child relationship rules are violated
     * @throws StorageNotFoundException if the specified parent storage doesn't exist
     */
    StorageDTO create(StorageCreateDTO storageCreateDTO);

    /**
     * Retrieves a storage by its unique identifier.
     *
     * @param storageId The ID of the storage to retrieve
     * @return The storage as a DTO
     * @throws StorageNotFoundException if no storage with the given ID exists
     */
    StorageDTO getById(Long storageId);

    /**
     * Retrieves all storages, optionally filtered by type.
     *
     * @param type Optional type filter; if null, returns all storages
     * @return List of storages as DTOs
     */
    List<StorageDTO> getAll(StorageType type);

    /**
     * Updates the basic properties of a storage (name and/or type).
     * Type changes are validated against the storage hierarchy rules.
     *
     * @param id ID of the storage to update
     * @param dto DTO containing the properties to update
     * @return The updated storage as a DTO
     * @throws StorageNotFoundException if no storage with the given ID exists
     * @throws IllegalArgumentException if type change violates hierarchy rules
     */
    StorageDTO update(Long id, StorageUpdateDTO dto);

    /**
     * Deletes a storage by its ID.
     *
     * @param storageId ID of the storage to delete
     * @throws StorageNotFoundException if no storage with the given ID exists
     */
    void delete(Long storageId);

    /**
     * Retrieves all items contained in a storage and all its sub-storages recursively.
     *
     * @param storageId ID of the storage to retrieve items from
     * @return List of all items as DTOs
     * @throws StorageNotFoundException if no storage with the given ID exists
     */
    List<ItemDTO> getAllItemDTOs(Long storageId);

    /**
     * Retrieves all direct sub-storages of a parent storage.
     *
     * @param parentId ID of the parent storage
     * @return List of direct child storages as DTOs
     * @throws StorageNotFoundException if no storage with the given parent ID exists
     */
    List<StorageDTO> getSubStorages(Long parentId);

    /**
     * Adds existing items to a storage.
     *
     * @param storageId The ID of the storage to add items to
     * @param itemIds   IDs of the items to add
     * @return Updated storage as a DTO
     * @throws StorageNotFoundException if the storage does not exist
     */
    StorageDTO addItems(Long storageId, List<Long> itemIds);

    /**
     * Removes specific items from a storage.
     *
     * @param storageId The ID of the storage
     * @param itemIds   IDs of the items to remove
     * @return Updated storage as a DTO
     * @throws StorageNotFoundException if the storage does not exist
     */
    StorageDTO removeItems(Long storageId, List<Long> itemIds);

    /**
     * Searches storages by partial name match (case-insensitive), optionally filtering by type.
     *
     * @param name The partial name to search for
     * @param type Optional storage type filter
     * @return List of matching storages
     */
    List<StorageDTO> searchByNameAndType(String name, StorageType type);
}
