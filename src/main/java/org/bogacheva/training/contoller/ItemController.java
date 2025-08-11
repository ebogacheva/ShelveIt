package org.bogacheva.training.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemUpdateDTO;
import org.bogacheva.training.service.item.search.ItemSearchService;
import org.bogacheva.training.service.item.crud.ItemService;
import org.bogacheva.training.service.dto.ItemDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemSearchService itemSearchService;

    @PostMapping
    public ResponseEntity<ItemDTO> create(@Valid @RequestBody ItemCreateDTO itemCreateDTO) {
        ItemDTO newItem = itemService.create(itemCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
    }

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAll() {
        return ResponseEntity.ok(itemService.getAll());
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDTO> get(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getById(itemId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> searchByKeywords(@RequestParam List<String> keywords) {
        List<ItemDTO> items = itemSearchService.searchItemsByKeywords(keywords);
        return ResponseEntity.ok(items);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItemDTO> update(
            @PathVariable Long itemId,
            @Valid @RequestBody ItemUpdateDTO itemUpdateDTO) {
        return ResponseEntity.ok(itemService.update(itemId, itemUpdateDTO));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{itemId}/near")
    public ResponseEntity<List<ItemDTO>> getItemsNear(@PathVariable Long itemId) {
        List<ItemDTO> nearItems = itemService.getItemsNear(itemId);
        return ResponseEntity.ok(nearItems);
    }

    @GetMapping("/{itemId}/trackStorages")
    public ResponseEntity<List<Long>> trackStorages(@PathVariable Long itemId) {
        List<Long> storageIds = itemSearchService.getStorageHierarchyIds(itemId);
        return ResponseEntity.ok(storageIds);
    }
}
