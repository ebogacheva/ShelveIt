package org.bogacheva.training.contoller.web;

import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.dto.StorageUpdateDTO;
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
        List<StorageDTO> subStorages = storageService.getSubStorages(id);
        List<ItemDTO> items = itemSearchService.getByStorageId(id);

        model.addAttribute("storage", storage);
        model.addAttribute("substorages", subStorages);
        model.addAttribute("items", items);
        return "storages/detail";
    }


    @GetMapping("/create")
    public String createStorageForm(@RequestParam(value = "parentId", required = false) Long parentId, Model model) {
        StorageCreateDTO storageCreateDTO = new StorageCreateDTO();
        if (parentId != null) {
            storageCreateDTO.setParentId(parentId);
        }
        model.addAttribute("storage", storageCreateDTO);
        return "storages/create";
    }

    @PostMapping
    public String createStorage(@ModelAttribute StorageCreateDTO storageCreateDTO) {
        storageService.create(storageCreateDTO);
        return "redirect:/storages";
    }

    @PostMapping("/{id}/delete")
    public String deleteStorage(@PathVariable Long id,
                                @RequestHeader(value = "Referer", defaultValue = "/storages") String referer) {
        storageService.delete(id);
        
        // If deleting from details page, redirect to list instead of back to details
        if (referer.contains("/storages/" + id) && !referer.contains("/storages/" + id + "/")) {
            return "redirect:/storages";
        }
        
        return "redirect:" + referer;
    }

    @GetMapping("/search")
    public String searchStoragesForm(Model model) {
        model.addAttribute("storageTypes", StorageType.values());
        return "storages/search";
    }

    @PostMapping("/search")
    public String searchStorages(@RequestParam(value = "name", required = false) String name,
                                @RequestParam(value = "type", required = false) StorageType type,
                                Model model) {
        List<StorageDTO> results = storageService.searchByNameAndType(name, type);
        model.addAttribute("results", results);
        model.addAttribute("searchName", name);
        model.addAttribute("searchType", type);
        model.addAttribute("storageTypes", StorageType.values());
        return "storages/search";
    }

    @GetMapping("/{id}/update")
    public String updateStorageForm(@PathVariable Long id, Model model) {
        StorageDTO storage = storageService.getById(id);
        model.addAttribute("storage", storage);
        model.addAttribute("storageTypes", StorageType.values());
        return "storages/update";
    }

    @PostMapping("/{id}/update")
    public String updateStorage(@PathVariable Long id, 
                              @RequestParam(value = "name", required = false) String name,
                              @RequestParam(value = "type", required = false) StorageType type) {
        StorageUpdateDTO storageUpdateDTO = new StorageUpdateDTO();
        storageUpdateDTO.setName(name);
        storageUpdateDTO.setType(type);
        
        storageService.update(id, storageUpdateDTO);
        return "redirect:/storages/" + id;
    }
}
