package org.bogacheva.training.contoller.web;

import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.item.crud.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/items")
public class ItemWebController {

    private final ItemService itemService;

    public ItemWebController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String listItems(Model model) {
        model.addAttribute("items", itemService.getAll());
        return "items/list";
    }

    @GetMapping("/{itemId}")
    public String getItem(@PathVariable Long itemId, Model model) {
        model.addAttribute("item", itemService.getById(itemId));
        return "items/detail";
    }

    @GetMapping("/create")
    public String createItemForm(Model model) {
        model.addAttribute("item", new ItemCreateDTO());
        return "items/create";
    }

    @PostMapping
    public String createItem(@ModelAttribute ItemCreateDTO itemCreateDTO) {
        itemService.create(itemCreateDTO);
        return "redirect:/items";
    }
}
