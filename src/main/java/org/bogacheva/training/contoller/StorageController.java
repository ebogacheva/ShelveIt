package org.bogacheva.training.contoller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageUpdateDTO;
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

    @GetMapping("/{id}")
    public ResponseEntity<StorageDTO> getStorageById(@PathVariable @Min(1) Long id) {
        StorageDTO storage = storageService.getById(id);
        return ResponseEntity.ok(storage);
    }

    @GetMapping
    public ResponseEntity<List<StorageDTO>> getAllStorages() {
        List<StorageDTO> storages = storageService.getAll();
        return ResponseEntity.ok(storages);
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<ItemDTO>> getAllItems(@PathVariable Long id) {
        List<ItemDTO> items = storageService.getAllItemDTOs(id);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}/substorages")
    public ResponseEntity<List<StorageDTO>> getSubStorages(@PathVariable Long id) {
        List<StorageDTO> substorages = storageService.getSubStorages(id);
        return ResponseEntity.ok(substorages);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StorageDTO> updateStorageProperties(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid StorageUpdateDTO updateDTO) {
        StorageDTO updated = storageService.updateProperties(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

//    //TODO: Implement in the service
//    @GetMapping("/search")
//    public ResponseEntity<List<StorageDTO>> searchStorages(
//            @RequestParam String name) {
//        List<StorageDTO> matches = storageService.findByNameContaining(name);
//        return ResponseEntity.ok(matches);
//    }

//    //TODO: Implement in the service
//    @PatchMapping("/{id}/move-to/{newParentId}")
//    public ResponseEntity<StorageDTO> moveStorage(
//            @PathVariable @Min(1) Long id,
//            @PathVariable @Min(1) Long newParentId) {
//        StorageDTO updatedStorage = storageService.moveToNewParent(id, newParentId);
//        return ResponseEntity.ok(updatedStorage);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id) {
        storageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/move")
    public ResponseEntity<Void> deleteAndMoveContents(
            @PathVariable @Min(1) Long id,
            @RequestParam(value = "targetStorageId", required = false) @Min(1) Long targetStorageId) {
        storageService.deleteAndMoveContents(id, targetStorageId);
        return ResponseEntity.noContent().build();
    }

//    //TODO: Implement in the service
//    @PostMapping("/{id}/items")
//    public ResponseEntity<StorageDTO> addItemsToStorage(
//            @PathVariable @Min(1) Long id,
//            @RequestBody List<Long> itemIds) {
//        StorageDTO updated = storageService.addItems(id, itemIds);
//        return ResponseEntity.ok(updated);
//    }
//
//    //TODO: Implement in the service
//    @DeleteMapping("/{id}/items")
//    public ResponseEntity<StorageDTO> removeItemsFromStorage(
//            @PathVariable @Min(1) Long id,
//            @RequestBody List<Long> itemIds) {
//        StorageDTO updated = storageService.removeItems(id, itemIds);
//        return ResponseEntity.ok(updated);
//    }
//
//    //TODO: Implement in the service
//    @PostMapping("/{id}/substorages")
//    public ResponseEntity<StorageDTO> addSubstorage(
//            @PathVariable Long id,
//            @RequestBody List<Long> substorageIds) {
//        StorageDTO updated = storageService.addSubstorages(id, substorageIds);
//        return ResponseEntity.ok(updated);
//    }
//
//    //TODO: Implement in the service
//    @DeleteMapping("/{id}/substorages")
//    public ResponseEntity<StorageDTO> removeSubstorages(
//            @PathVariable @Min(1) Long id,
//            @PathVariable List<Long> substorageIds) {
//        StorageDTO updated = storageService.removeSubstorages(id, substorageIds);
//        return ResponseEntity.ok(updated);
//    }
}
