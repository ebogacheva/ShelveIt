package org.bogacheva.training.contoller;

import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.item.ItemService;
import org.bogacheva.training.service.dto.ItemDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    public void create(ItemCreateDTO itemCreateDTO) {
        itemService.create(itemCreateDTO);
    }

    public List<ItemDTO> getAll() {
        return itemService.getAll();
    }

    public ItemDTO get(Long itemId) {
        return itemService.getById(itemId);
    }

    public void delete(Long itemId) {
        itemService.delete(itemId);
    }
}
