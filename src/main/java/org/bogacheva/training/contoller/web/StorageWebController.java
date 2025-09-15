package org.bogacheva.training.contoller.web;

import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.item.crud.ItemService;
import org.bogacheva.training.service.item.search.ItemSearchService;
import org.bogacheva.training.service.storage.StorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/storages")
public class StorageWebController {

    private final StorageService storageService;
    private final ItemSearchService itemSearchService;

    public StorageWebController(StorageService storageService, ItemSearchService itemSearchService) {
        this.storageService = storageService;
        this.itemSearchService = itemSearchService;
    }

    @GetMapping
    public String listStorages(Model model) {
        model.addAttribute("storages", storageService.getAll(null));
        return "storages/list";
    }

    @GetMapping("/{id}")
    public String getStorageDetails(@PathVariable Long id, Model model) {
        StorageDTO storage = storageService.getById(id);
        List<StorageDTO> substorages = storageService.getSubStorages(id);
        List<ItemDTO> items = itemSearchService.getByStorageId(id);

        model.addAttribute("storage", storage);
        model.addAttribute("substorages", substorages);
        model.addAttribute("items", items);
        return "storages/detail";
    }


    @GetMapping("/create")
    public String createStorageForm(Model model) {
        model.addAttribute("storage", new StorageCreateDTO());
        return "storages/create";
    }

    @PostMapping
    public String createStorage(@ModelAttribute StorageCreateDTO storageCreateDTO) {
        storageService.create(storageCreateDTO);
        return "redirect:/storages";
    }
}
