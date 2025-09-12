package org.bogacheva.training.contoller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/storages")
@RequiredArgsConstructor
@Tag(name = "Storage API", description = "API for managing storages and their items")
public class StorageController {

    private final StorageService storageService;

    @Operation(summary = "Create a new storage",
            description = "Creates a new storage entity. RESIDENCE type must not have parent, other types require a parent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Storage created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or storage hierarchy")
    })
    @PostMapping
    public ResponseEntity<StorageDTO> create(
            @RequestBody @Valid
            @Parameter(description = "Storage data to create", required = true)
            StorageCreateDTO storageCreateDTO) {
        StorageDTO createdStorage = storageService.create(storageCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStorage);
    }

    @Operation(summary = "Get a storage by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Storage found"),
            @ApiResponse(responseCode = "404", description = "Storage not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StorageDTO> get(
            @PathVariable @Min(1)
            @Parameter(description = "ID of the storage to retrieve", required = true)
            Long id) {
        StorageDTO storage = storageService.getById(id);
        return ResponseEntity.ok(storage);
    }

    @Operation(summary = "Get list of storages", description = "Optionally filtered by type or all existed")
    @ApiResponse(responseCode = "200", description = "List of storages")
    @GetMapping
    public ResponseEntity<List<StorageDTO>> getAll(
            @RequestParam(required = false)
            @Parameter(description = "Filter storages by type", example = "ROOM")
            StorageType type) {
        List<StorageDTO> storages = storageService.getAll(type);
        return ResponseEntity.ok(storages);
    }

    @Operation(summary = "Update storage by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Storage updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or hierarchy"),
            @ApiResponse(responseCode = "404", description = "Storage not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<StorageDTO> update(
            @Parameter(description = "ID of storage to update", required = true)
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid
            @Parameter(description = "Updated storage data", required = true)
            StorageUpdateDTO updateDTO) {
        StorageDTO updated = storageService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get all items in a storage")
    @ApiResponse(responseCode = "200", description = "List of items")
    @GetMapping("/{id}/items")
    public ResponseEntity<List<ItemDTO>> getAllItems(
            @Parameter(description = "ID of storage to retrieve items from", required = true)
            @PathVariable Long id) {
        List<ItemDTO> items = storageService.getAllItemDTOs(id);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Get sub-storages of a storage")
    @ApiResponse(responseCode = "200", description = "List of sub-storages")
    @GetMapping("/{id}/substorages")
    public ResponseEntity<List<StorageDTO>> getSubStorages(
            @Parameter(description = "ID of parent storage", required = true)
            @PathVariable Long id) {
        List<StorageDTO> substorages = storageService.getSubStorages(id);
        return ResponseEntity.ok(substorages);
    }


    @Operation(summary = "Delete a storage by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Storage deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Storage not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of storage to delete", required = true)
            @PathVariable @Min(1) Long id) {
        storageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add items to a storage")
    @ApiResponse(responseCode = "200", description = "Items added successfully")
    @PostMapping("/{id}/items")
    public ResponseEntity<StorageDTO> addItemsToStorage(
            @Parameter(description = "ID of storage to add items to", required = true)
            @PathVariable @Min(1) Long id,
            @Parameter(description = "List of item IDs to add", required = true)
            @RequestBody List<Long> itemIds) {
        StorageDTO updated = storageService.addItems(id, itemIds);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Remove items from a storage")
    @ApiResponse(responseCode = "200", description = "Items removed successfully")
    @DeleteMapping("/{id}/items")
    public ResponseEntity<StorageDTO> removeItemsFromStorage(
            @Parameter(description = "ID of storage to remove items from", required = true)
            @PathVariable @Min(1) Long id,
            @Parameter(description = "List of item IDs to remove", required = true)
            @RequestBody List<Long> itemIds) {
        StorageDTO updated = storageService.removeItems(id, itemIds);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Search storages by name and type")
    @ApiResponse(responseCode = "200", description = "List of storages matching criteria")
    @GetMapping("/search")
    public ResponseEntity<List<StorageDTO>> searchStorages(
            @Parameter(description = "Name filter", required = true)
            @RequestParam String name,
            @Parameter(description = "Type filter", example = "ROOM")
            @RequestParam(required = false) StorageType type) {
        List<StorageDTO> storages = storageService.searchByNameAndType(name, type);
        return ResponseEntity.ok(storages);
    }
}
