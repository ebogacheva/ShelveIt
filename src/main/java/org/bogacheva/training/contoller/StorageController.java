package org.bogacheva.training.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.storage.DefaultStorageService;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storages")
@RequiredArgsConstructor
public class StorageController {

    private final DefaultStorageService storageService;

    @PostMapping
    public ResponseEntity<StorageDTO> create(@RequestBody @Valid StorageCreateDTO storageCreateDTO) {
        StorageDTO createdStorage = storageService.create(storageCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStorage);
    }

    @GetMapping("/id")
    public ResponseEntity<StorageDTO> getStorageById(@PathVariable Long storageId) {
        StorageDTO storage = storageService.getById(storageId);
        return ResponseEntity.ok(storage);
    }

    @GetMapping
    public ResponseEntity<List<StorageDTO>> getAllStorages() {
        List<StorageDTO> storages = storageService.getAll();
        return ResponseEntity.ok(storages);
    }

    @GetMapping("/{storageId}/items")
    public ResponseEntity<List<ItemDTO>> getAllItems(@PathVariable Long storageId) {
        List<ItemDTO> items = storageService.getAllItemDTOs(storageId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{storageId}/substorages")
    public ResponseEntity<List<StorageDTO>> getSubStorages(@PathVariable Long storageId) {
        List<StorageDTO> substorages = storageService.getSubStorages(storageId);
        return ResponseEntity.ok(substorages);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Long storageId) {
        storageService.delete(storageId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/move")
    public ResponseEntity<Void> deleteAndMoveContents(
            @PathVariable Long id,
            @RequestParam(value = "targetStorageId", required = false) Long targetStorageId) {
        storageService.deleteAndMoveContents(id, targetStorageId);
        return ResponseEntity.noContent().build();
    }
}
