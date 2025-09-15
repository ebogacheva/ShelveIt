package org.bogacheva.training.service.item.crud;

import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.ItemUpdateDTO;
import org.bogacheva.training.exceptions.ItemNotFoundException;
import org.bogacheva.training.exceptions.StorageNotFoundException;

import java.util.List;

public interface ItemService {
    /**
     * Create a new item.
     *
     * @param itemCreateDTO Data for creating the item.
     * @return The created item as ItemDTO.
     * @throws StorageNotFoundException if the specified in dto storage does not exist.
     */
    ItemDTO create(ItemCreateDTO itemCreateDTO);

    /**
     * Get an item by its ID.
     *
     * @param itemId The ID of the item to retrieve.
     * @return The item as ItemDTO.
     * @throws ItemNotFoundException if the item with given ID does not exist.
     */
    ItemDTO getById(Long itemId);

    /**
     * Get all items.
     *
     * @return A list of all items as ItemDTOs.
     */
    List<ItemDTO> getAll();

    /**
     * Delete an item by its ID.
     *
     * @param itemId The ID of the item to delete.
     * @throws ItemNotFoundException if the item does not exist.
     */
    void delete(Long itemId);

    /**
     * Update an existing item.
     *
     * @param itemId        The ID of the item to update.
     * @param itemUpdateDTO Data for updating the item.
     * @return The updated item as ItemDTO.
     * @throws ItemNotFoundException    if the item does not exist.
     * @throws StorageNotFoundException if the new storage does not exist.
     * @throws IllegalArgumentException if no fields are provided for update.
     */
    ItemDTO update(Long itemId, ItemUpdateDTO itemUpdateDTO);
}
