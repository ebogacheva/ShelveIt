package org.bogacheva.training.contoller.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageUpdateDTO;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.storage.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing storages in the ShelveIt application.
 * Exposes endpoints to create, retrieve, update, delete storages,
 * manage items within storages, and search storages by name/type.
 */
@RestController
@RequestMapping("/api/storages")
@RequiredArgsConstructor
public class StorageRestController {

    private final StorageService storageService;

    @PostMapping
    public ResponseEntity<StorageDTO> create(
            @RequestBody @Valid
            StorageCreateDTO storageCreateDTO) {
        StorageDTO createdStorage = storageService.create(storageCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStorage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageDTO> get(
            @PathVariable @Min(1)
            Long id) {
        StorageDTO storage = storageService.getById(id);
        return ResponseEntity.ok(storage);
    }

    @GetMapping
    public ResponseEntity<List<StorageDTO>> getAll(
            @RequestParam(required = false)
            StorageType type) {
        List<StorageDTO> storages = storageService.getAll(type);
        return ResponseEntity.ok(storages);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StorageDTO> update(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid
            StorageUpdateDTO updateDTO) {
        StorageDTO updated = storageService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<ItemDTO>> getAllItems(
            @PathVariable Long id) {
        List<ItemDTO> items = storageService.getAllItemDTOs(id);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}/substorages")
    public ResponseEntity<List<StorageDTO>> getSubStorages(
            @PathVariable Long id) {
        List<StorageDTO> substorages = storageService.getSubStorages(id);
        return ResponseEntity.ok(substorages);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Min(1) Long id) {
        storageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<StorageDTO> addItemsToStorage(
            @PathVariable @Min(1) Long id,
            @RequestBody List<Long> itemIds) {
        StorageDTO updated = storageService.addItems(id, itemIds);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/items")
    public ResponseEntity<StorageDTO> removeItemsFromStorage(
            @PathVariable @Min(1) Long id,
            @RequestBody List<Long> itemIds) {
        StorageDTO updated = storageService.removeItems(id, itemIds);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/search")
    public ResponseEntity<List<StorageDTO>> searchStorages(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) StorageType type) {
        List<StorageDTO> storages = storageService.searchByNameAndType(name, type);
        return ResponseEntity.ok(storages);
    }
}
