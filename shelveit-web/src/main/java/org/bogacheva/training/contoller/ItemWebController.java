package org.bogacheva.training.contoller;

import org.bogacheva.training.client.RestClient;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.ItemUpdateDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemWebController {

    private final RestClient restClient;

    public ItemWebController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping
    public String listItems(Model model) {
        model.addAttribute("items", restClient.getAllItems());
        return "items/list";
    }

    @GetMapping("/{itemId}")
    public String getItem(@PathVariable Long itemId, Model model) {
        ItemDTO item = restClient.getItemById(itemId);
        List<Long> storageHierarchyIds = restClient.getStorageHierarchyIds(itemId);
        List<ItemDTO> nearItems = restClient.getItemsNear(itemId);
        
        model.addAttribute("item", item);
        model.addAttribute("storageHierarchyIds", storageHierarchyIds);
        model.addAttribute("nearItems", nearItems);
        return "items/detail";
    }

    @GetMapping("/create")
    public String createItemForm(@RequestParam(value = "storageId", required = false) Long storageId, Model model) {
        ItemCreateDTO itemCreateDTO = new ItemCreateDTO();
        if (storageId != null) {
            itemCreateDTO.setStorageId(storageId);
        }
        model.addAttribute("item", itemCreateDTO);
        return "items/create";
    }

    @PostMapping
    public String createItem(@ModelAttribute ItemCreateDTO itemCreateDTO) {
        restClient.createItem(itemCreateDTO);
        return "redirect:/items";
    }

    @PostMapping("/{itemId}/delete")
    public String deleteItem(@PathVariable Long itemId,
                             @RequestHeader(value = "Referer", defaultValue = "/items") String referer) {
        restClient.deleteItem(itemId);
        
        // If deleting from details page, redirect to list instead of back to details
        if (referer.contains("/items/" + itemId) && !referer.contains("/items/" + itemId + "/")) {
            return "redirect:/items";
        }
        
        return "redirect:" + referer;
    }

    @GetMapping("/search")
    public String searchItemsForm(Model model) {
        return "items/search";
    }

    @PostMapping("/search")
    public String searchItems(@RequestParam(value = "name", required = false) String name,
                             @RequestParam(value = "keywords", required = false) String keywords,
                             Model model) {
        List<String> keywordList = null;
        if (keywords != null && !keywords.trim().isEmpty()) {
            keywordList = List.of(keywords.split(",\\s*"));
        }
        
        List<ItemDTO> results = restClient.searchItems(name, keywordList);
        model.addAttribute("results", results);
        model.addAttribute("searchName", name);
        model.addAttribute("searchKeywords", keywords);
        return "items/search";
    }

    @GetMapping("/{itemId}/update")
    public String updateItemForm(@PathVariable Long itemId, Model model) {
        ItemDTO item = restClient.getItemById(itemId);
        model.addAttribute("item", item);
        return "items/update";
    }

    @PostMapping("/{itemId}/update")
    public String updateItem(@PathVariable Long itemId, 
                           @RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "storageId", required = false) Long storageId,
                           @RequestParam(value = "keywords", required = false) String keywords) {
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO();
        itemUpdateDTO.setName(name);
        itemUpdateDTO.setStorageId(storageId);
        updateKeywords(keywords, itemUpdateDTO);
        restClient.updateItem(itemId, itemUpdateDTO);
        return "redirect:/items/" + itemId;
    }

    private void updateKeywords(String keywords, ItemUpdateDTO itemUpdateDTO) {
        if (keywords != null && !keywords.trim().isEmpty()) {
            itemUpdateDTO.setKeywords(List.of(keywords.split(",\\s*")));
        }
    }
}
