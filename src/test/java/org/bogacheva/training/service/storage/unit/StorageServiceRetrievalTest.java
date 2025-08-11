package org.bogacheva.training.service.storage.unit;

import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.exceptions.StorageNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class StorageServiceRetrievalTest extends StorageServiceBaseTest {

    @Test
    @DisplayName("Should return StorageDTO with expected values when valid ID is provided")
    void getById_shouldReturnStorageDTO_whenValidIdProvided() {
        // Arrange
        Long storageId = 1L;
        Storage storage = createStorage(storageId, "Residence", StorageType.RESIDENCE, null);
        StorageDTO expectedDTO = createStorageDTO(storageId, "Residence", StorageType.RESIDENCE, null);

        when(storageRepo.findById(storageId)).thenReturn(Optional.of(storage));
        when(storageMapper.toDTO(storage)).thenReturn(expectedDTO);

        // Act
        StorageDTO result = storageService.getById(storageId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO.getId(), result.getId());
        assertEquals(expectedDTO.getName(), result.getName());
        assertEquals(expectedDTO.getType(), result.getType());

        verify(storageRepo).findById(storageId);
        verify(storageMapper).toDTO(storage);
    }

    @Test
    @DisplayName("Should throw StorageNotFoundException when invalid ID is provided")
    void getById_shouldThrowException_whenInvalidIdProvided() {
        // Arrange
        Long invalidId = 999L;
        when(storageRepo.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        StorageNotFoundException exception = assertThrows(
                StorageNotFoundException.class,
                () -> storageService.getById(invalidId));

        assertEquals("Storage with ID: 999 was not found.", exception.getMessage());

        verify(storageRepo).findById(invalidId);
        verify(storageMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Should return all StorageDTOs when getAll is called")
    void getAll_shouldReturnAllStorages() {
        // Arrange
        Storage home = createStorage(1L, "Home", StorageType.RESIDENCE, null);
        Storage office = createStorage(2L, "Office", StorageType.RESIDENCE, null);
        Storage room = createStorage(3L, "Room", StorageType.ROOM, home);
        List<Storage> storages = List.of(home, office, room);

        StorageDTO homeDTO = createStorageDTO(1L, "Home", StorageType.RESIDENCE, null);
        StorageDTO officeDTO = createStorageDTO(2L, "Office", StorageType.RESIDENCE, null);
        StorageDTO roomDTO = createStorageDTO(3L, "Room", StorageType.ROOM, 1L);
        List<StorageDTO> expectedDTOs = List.of(homeDTO, officeDTO, roomDTO);

        when(storageRepo.findAll()).thenReturn(storages);
        when(storageMapper.toDTOList(storages)).thenReturn(expectedDTOs);

        // Act
        List<StorageDTO> results = storageService.getAll();

        // Assert
        assertNotNull(results);
        assertEquals(3, results.size());

        verify(storageRepo).findAll();
        verify(storageMapper).toDTOList(storages);
    }

    @Test
    @DisplayName("Should return List of sub-storages when parent exists")
    void getSubStorages_shouldReturnSubStorages_whenParentExists() {
        // Arrange
        Long parentId = 1L;
        Storage home = createStorage(parentId, "Home", StorageType.RESIDENCE, null);
        Storage room = createStorage(2L, "Room", StorageType.ROOM, home);
        Storage kitchen = createStorage(3L, "Kitchen", StorageType.ROOM, home);
        List<Storage> subStorages  = List.of(room, kitchen);

        StorageDTO roomDTO = createStorageDTO(2L, "Room", StorageType.ROOM, parentId);
        StorageDTO kitchenDTO = createStorageDTO(3L, "Kitchen", StorageType.ROOM, parentId);
        List<StorageDTO> expectedDTOs = List.of(roomDTO, kitchenDTO);

        when(storageRepo.findById(parentId)).thenReturn(Optional.of(home));
        when(storageRepo.findByParentId(parentId)).thenReturn(subStorages);
        when(storageMapper.toDTOList(subStorages)).thenReturn(expectedDTOs);

        // Act
        List<StorageDTO> results = storageService.getSubStorages(parentId);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());

        verify(storageRepo).findById(parentId);
        verify(storageRepo).findByParentId(parentId);
        verify(storageMapper).toDTOList(subStorages);
    }

    @Test
    @DisplayName("Should throw StorageNotFoundException when parent storage not found")
    void getSubStorages_shouldThrowException_whenParentNotFound() {
        // Arrange
        Long invalidParentId = 999L;
        when(storageRepo.findById(invalidParentId)).thenReturn(Optional.empty());

        // Act & Assert
        StorageNotFoundException exception = assertThrows(
                StorageNotFoundException.class,
                () -> storageService.getSubStorages(invalidParentId));

        assertEquals("Storage with ID: 999 was not found.", exception.getMessage());

        verify(storageRepo).findById(invalidParentId);
        verify(storageRepo, never()).findByParentId(any());
        verify(storageMapper, never()).toDTOList(any());
    }

    @Test
    @DisplayName("Should return all ItemDTOs from the descendant storage hierarchy")
    void getAllItemDTOs_shouldReturnAllItems_fromStorageHierarchy() {
        // Arrange
        Long residenceId = 1L;
        Long roomId = 2L;
        Storage residence = createStorage(residenceId, "Residence", StorageType.RESIDENCE, null);
        Storage room = createStorage(roomId, "Room", StorageType.ROOM, residence);
        Item item1 = createItem(1L, "Item 1", residence);
        Item item2 = createItem(2L, "Item 2", residence);
        List<Item> residenceItems = List.of(item1, item2);
        Item item3 = createItem(3L, "Item 3", room);
        List<Item> roomItems = List.of(item3);

        residence.setItems(residenceItems);
        room.setItems(residenceItems);

        // Create expected DTOs
        StorageDTO roomStorageDTO = createStorageDTO(roomId, "Room", StorageType.ROOM, residenceId);
        StorageDTO residenceStorageDTO = createStorageDTO(residenceId, "Residence", StorageType.RESIDENCE, null);

        ItemDTO dto1 = createItemDTO(1L, "Item 1", residenceStorageDTO);
        ItemDTO dto2 = createItemDTO(2L, "Item 2", residenceStorageDTO);
        ItemDTO dto3 = createItemDTO(3L, "Item 3", roomStorageDTO);

        List<ItemDTO> expectedDTOs = List.of(dto1, dto2, dto3);

        when(storageRepo.findById(residenceId)).thenReturn(Optional.of(residence));
        when(itemMapper.toDTOList(anyList())).thenReturn(expectedDTOs);

        // Act
        List<ItemDTO> results = storageService.getAllItemDTOs(residenceId);

        // Assert
        assertNotNull(results);
        assertEquals(3, results.size());

        verify(storageRepo).findById(residenceId);
        verify(itemMapper).toDTOList(anyList());
    }

    @Test
    @DisplayName("Should throw StorageNotFoundException when storage doesn't exist.")
    void getAllItemDTOs_shouldThrowException_whenStorageNotFound() {
        // Arrange
        Long invalidId = 999L;
        when(storageRepo.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        StorageNotFoundException exception = assertThrows(
                StorageNotFoundException.class,
                () -> storageService.getAllItemDTOs(invalidId));

        assertEquals("Storage with ID: 999 was not found.", exception.getMessage());

        verify(storageRepo).findById(invalidId);
        verify(itemMapper, never()).toDTOList(any());
    }

}
