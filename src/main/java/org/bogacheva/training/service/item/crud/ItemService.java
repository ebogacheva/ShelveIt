package org.bogacheva.training.service.item.crud;

import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.ItemUpdateDTO;

import java.util.List;

public interface ItemService {
    ItemDTO create(ItemCreateDTO itemCreateDTO);

    ItemDTO getById(Long itemId);

    List<ItemDTO> getAll();

    void delete(Long itemId);

    ItemDTO update(Long itemId, ItemUpdateDTO itemUpdateDTO);

    List<ItemDTO> getItemsNear(Long itemId);

}
