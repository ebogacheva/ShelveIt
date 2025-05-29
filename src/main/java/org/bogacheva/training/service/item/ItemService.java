package org.bogacheva.training.service.item;

import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.ItemUpdateDTO;

import java.util.List;

public interface ItemService {
    ItemDTO create(ItemCreateDTO itemCreateDTO);

    ItemDTO getById(Long itemId);

    List<ItemDTO> getAll();

    void delete(Long itemId);

    List<ItemDTO> getByStorageId(Long storageId);

    List<ItemDTO> findByKeywords(List<String> keywords);

    ItemDTO update(Long itemId, ItemUpdateDTO itemUpdateDTO);
}
