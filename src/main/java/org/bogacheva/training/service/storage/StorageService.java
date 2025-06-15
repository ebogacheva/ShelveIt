package org.bogacheva.training.service.storage;

import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.dto.StorageUpdateDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StorageService {
    StorageDTO create(StorageCreateDTO storageCreateDTO);

    StorageDTO getById(Long storageId);

    void delete(Long storageId);

    List<StorageDTO> getAll();

    List<ItemDTO> getAllItemDTOs(Long storageId);

    List<StorageDTO> getSubStorages(Long parentId);

    void deleteAndMoveContents(Long storageId, Long targetStorageId);

    @Transactional
    StorageDTO updateProperties(Long id, StorageUpdateDTO updateDTO);

}
