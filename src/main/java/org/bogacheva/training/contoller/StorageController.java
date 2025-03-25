package org.bogacheva.training.contoller;

import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.storage.StorageService;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/storages")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    public void create(StorageCreateDTO storageCreateDTO) {
        this.storageService.create(storageCreateDTO);
    }

    public StorageDTO get(Long storageId) {
        return storageService.getById(storageId);
    }

    public List<StorageDTO> getAll() {
        return storageService.getAll();
    }

    public List<ItemDTO> getAllItems(Long storageId) {
        return storageService.getAllItemDTOs(storageId);
    }

    public void delete(Long storageId) {
        storageService.delete(storageId);
    }
}
