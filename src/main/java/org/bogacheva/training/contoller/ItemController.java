package org.bogacheva.training.contoller;

import lombok.RequiredArgsConstructor;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.item.ItemService;
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

    @PostMapping
    public ResponseEntity<ItemDTO> create(@RequestBody ItemCreateDTO itemCreateDTO) {
        ItemDTO newItem = itemService.create(itemCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
    }

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAll() {
        List<ItemDTO> items = itemService.getAll();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDTO> get(@PathVariable Long itemId) {
        ItemDTO itemDTO = itemService.getById(itemId);
        return ResponseEntity.ok(itemDTO);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
        return ResponseEntity.noContent().build();
    }
}
