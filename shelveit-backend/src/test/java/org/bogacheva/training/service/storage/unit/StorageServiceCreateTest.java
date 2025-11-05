package org.bogacheva.training.service.storage.unit;

import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.exceptions.InvalidStorageHierarchyException;
import org.bogacheva.training.exceptions.StorageNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class StorageServiceCreateTest extends StorageServiceBaseTest {

    @Test
    @DisplayName("Should return expected StorageDTO when valid StorageCreateDTO for RESIDENCE is provided")
    void create_shouldReturnStorageDTO_whenValidResidenceDTOProvided() {
        // Arrange
        StorageCreateDTO residenceDTO = createStorageCreateDTO("Residence", StorageType.RESIDENCE, null);
        Storage storage = createStorage(null,"Residence", StorageType.RESIDENCE, null);
        Storage savedStorage = createStorage(1L,"Residence", StorageType.RESIDENCE, null);
        StorageDTO expectedDTO = createStorageDTO(1L, "Residence", StorageType.RESIDENCE, null);

        doNothing().when(validator).validateStorageCreation(residenceDTO);
        when(storageMapper.toEntity(residenceDTO)).thenReturn(storage);
        when(storageRepo.save(any(Storage.class))).thenReturn(savedStorage);
        when(storageMapper.toDTO(any(Storage.class))).thenReturn(expectedDTO);

        // Act
        StorageDTO result = storageService.create(residenceDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO.getName(), result.getName());
        assertEquals(expectedDTO.getType(), result.getType());
        assertEquals(expectedDTO.getParentId(), result.getParentId());

        verify(validator).validateStorageCreation(residenceDTO);
        verify(storageMapper).toEntity(residenceDTO);
        verify(storageRepo).save(storage);
        verify(storageMapper).toDTO(savedStorage);
    }

    @Test
    @DisplayName("Should return expected StorageDTO when valid StorageCreateDTO for ROOM is provided")
    void create_shouldReturnStorageDTO_whenValidRoomDTOProvided() {
        // Arrange
        Long parentId = 1L;
        Storage parentStorage = createStorage(parentId, "Residence", StorageType.RESIDENCE, null);
        StorageCreateDTO roomCreateDTO = createStorageCreateDTO("Living Room", StorageType.ROOM, parentId);
        Storage room = createStorage(null, "Living Room", StorageType.ROOM, null);
        Storage savedRoom = createStorage(2L, "Living Room", StorageType.ROOM, parentStorage);
        StorageDTO expectedDTO = createStorageDTO(2L, "Living Room", StorageType.ROOM, parentId);

        doNothing().when(validator).validateStorageCreation(roomCreateDTO);
        when(storageRepo.findById(parentId)).thenReturn(Optional.of(parentStorage));
        doNothing().when(validator).validateHierarchySubStorageRules(parentStorage.getType(), roomCreateDTO.getType());
        when(storageMapper.toEntity(roomCreateDTO)).thenReturn(room);
        when(storageRepo.save(any(Storage.class))).thenReturn(savedRoom);
        when(storageMapper.toDTO(savedRoom)).thenReturn(expectedDTO);

        // Act
        StorageDTO result = storageService.create(roomCreateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO.getId(), result.getId());
        assertEquals(expectedDTO.getName(), result.getName());
        assertEquals(expectedDTO.getType(), result.getType());
        assertEquals(expectedDTO.getParentId(), result.getParentId());

        verify(validator).validateStorageCreation(roomCreateDTO);
        verify(storageRepo).findById(parentId);
        verify(validator).validateHierarchySubStorageRules(parentStorage.getType(), roomCreateDTO.getType());
        verify(storageMapper).toEntity(roomCreateDTO);
        verify(storageRepo).save(any(Storage.class));
        verify(storageMapper).toDTO(savedRoom);
    }

    @Test
    @DisplayName("Should throw InvalidStorageHierarchyException when ROOM is created without parent")
    void create_shouldThrowException_whenRoomCreateDTOWithNoParent() {
        // Arrange
        StorageCreateDTO invalidDTO = createStorageCreateDTO("Invalid", StorageType.ROOM, null);

        doThrow(new InvalidStorageHierarchyException("Storage type ROOM requires a parent"))
                .when(validator).validateStorageCreation(invalidDTO);

        // Act & Assert
        InvalidStorageHierarchyException exception = assertThrows(
                InvalidStorageHierarchyException.class,
                () -> storageService.create(invalidDTO));

        assertEquals("Storage type ROOM requires a parent", exception.getMessage());

        verify(validator).validateStorageCreation(invalidDTO);
        verify(storageMapper, never()).toEntity(any());
        verify(storageRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidStorageHierarchyException when RESIDENCE is created with parent")
    void create_shouldThrowException_whenResidenceCreateDTOWithParent() {
        // Arrange
        Long parentId = 1L;
        StorageCreateDTO invalidDTO = createStorageCreateDTO("Invalid", StorageType.RESIDENCE, parentId);

        doThrow(new InvalidStorageHierarchyException("RESIDENCE storage should not have a parent."))
                .when(validator).validateStorageCreation(invalidDTO);

        // Act & Assert
        InvalidStorageHierarchyException exception = assertThrows(
                InvalidStorageHierarchyException.class,
                () -> storageService.create(invalidDTO));

        assertEquals("RESIDENCE storage should not have a parent.", exception.getMessage());

        verify(validator).validateStorageCreation(invalidDTO);
        verify(storageMapper, never()).toEntity(any());
        verify(storageRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw StorageNotFoundException when parent storage with provided ID not found")
    void create_shouldThrowException_whenParentNotFound() {
        // Arrange
        Long nonExistentParentId = 999L;
        StorageCreateDTO roomDTO = createStorageCreateDTO("Room", StorageType.ROOM, nonExistentParentId);

        doNothing().when(validator).validateStorageCreation(roomDTO);
        when(storageRepo.findById(nonExistentParentId)).thenReturn(Optional.empty());

        // Act & Assert
        StorageNotFoundException exception = assertThrows(
                StorageNotFoundException.class,
                () -> storageService.create(roomDTO));

        assertEquals("Storage with ID: 999 was not found.", exception.getMessage());

        verify(validator).validateStorageCreation(roomDTO);
        verify(storageRepo).findById(nonExistentParentId);
        verify(storageMapper, never()).toEntity(any());
        verify(storageRepo, never()).save(any());
    }
}
