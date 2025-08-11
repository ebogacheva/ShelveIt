package org.bogacheva.training.service.storage.unit;

import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.dto.StorageUpdateDTO;
import org.bogacheva.training.service.exceptions.InvalidStorageHierarchyException;
import org.bogacheva.training.service.exceptions.StorageNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StorageServiceUpdateTest extends StorageServiceBaseTest {

    @Test
    @DisplayName("Should update name when valid DTO with new name provided")
    void updateProperties_shouldUpdateName_whenOnlyNameProvided() {
        // Arrange
        Long storageId = 1L;
        String currName = "Old Name";
        String updName = "New Name";
        StorageType storageType = StorageType.RESIDENCE;

        Storage currStorage = createStorage(storageId, currName, storageType, null);
        Storage updStorage = createStorage(storageId, updName, storageType, null);
        StorageDTO expectedDTO = createStorageDTO(storageId, updName, storageType, null);

        StorageUpdateDTO updateDTO = new StorageUpdateDTO();
        updateDTO.setName(updName);

        when(storageRepo.findById(storageId)).thenReturn(Optional.of(currStorage));
        when(storageRepo.save(any(Storage.class))).thenReturn(updStorage);
        when(storageMapper.toDTO(any(Storage.class))).thenReturn(expectedDTO);

        // Act
        StorageDTO result = storageService.updateProperties(storageId, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updName, result.getName());
        assertEquals(storageType, result.getType());

        verify(storageRepo).findById(storageId);
        verify(storageRepo).save(any(Storage.class));
        verify(storageMapper).toDTO(any(Storage.class));
        verify(validator, never()).validateTypeUpdate(any(), any());
    }

    @Test
    @DisplayName("Should update type when valid DTO with new type provided")
    void updateProperties_shouldUpdateType_whenOnlyTypeProvided() {
        // Arrange
        Long parentId = 1L;
        Storage parentStorage = createStorage(parentId, "Residence", StorageType.RESIDENCE, null);
        Long storageId = 2L;
        String name = "Storage Name";
        StorageType currType = StorageType.ROOM;
        StorageType updType = StorageType.FURNITURE;

        Storage existingStorage = createStorage(storageId, name, currType, parentStorage);
        Storage updatedStorage = createStorage(storageId, name, updType, parentStorage);
        StorageDTO expectedDTO = createStorageDTO(storageId, name, updType, parentId);

        StorageUpdateDTO updateDTO = new StorageUpdateDTO();
        updateDTO.setType(updType);

        when(storageRepo.findById(storageId)).thenReturn(Optional.of(existingStorage));
        doNothing().when(validator).validateTypeUpdate(any(Storage.class), eq(updType));
        when(storageRepo.save(any(Storage.class))).thenReturn(updatedStorage);
        when(storageMapper.toDTO(any(Storage.class))).thenReturn(expectedDTO);

        // Act
        StorageDTO result = storageService.updateProperties(storageId, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(updType, result.getType());

        verify(storageRepo).findById(storageId);
        verify(validator).validateTypeUpdate(any(Storage.class), eq(updType));
        verify(storageRepo).save(any(Storage.class));
        verify(storageMapper).toDTO(any(Storage.class));
    }

    @Test
    @DisplayName("Should throw exception when trying to update storage with invalid id")
    void updateProperties_shouldThrowException_whenStorageNotFound() {
        // Arrange
        Long nonExistentId = 999L;
        StorageUpdateDTO updateDTO = new StorageUpdateDTO();
        updateDTO.setName("New Name");

        when(storageRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        StorageNotFoundException exception = assertThrows(
                StorageNotFoundException.class,
                () -> storageService.updateProperties(nonExistentId, updateDTO));

        assertEquals("Storage with ID: 999 was not found.", exception.getMessage());

        verify(storageRepo).findById(nonExistentId);
        verify(storageRepo, never()).save(any());
        verify(storageMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Should throw exception when trying to update type violating hierarchy rules")
    void updateProperties_shouldThrowException_whenTypeUpdateValidationFails() {
        // Arrange
        Long storageId = 1L;
        String name = "Storage Name";
        StorageType originalType = StorageType.RESIDENCE;
        StorageType invalidType = StorageType.ROOM;

        Storage existingStorage = createStorage(storageId, name, originalType, null);

        StorageUpdateDTO updateDTO = new StorageUpdateDTO();
        updateDTO.setType(invalidType);

        when(storageRepo.findById(storageId)).thenReturn(Optional.of(existingStorage));
        doThrow(new InvalidStorageHierarchyException("ROOM storage requires a parent"))
                .when(validator).validateTypeUpdate(any(Storage.class), eq(invalidType));

        // Act & Assert
        InvalidStorageHierarchyException exception = assertThrows(
                InvalidStorageHierarchyException.class,
                () -> storageService.updateProperties(storageId, updateDTO));

        assertEquals("ROOM storage requires a parent", exception.getMessage());

        verify(storageRepo).findById(storageId);
        verify(validator).validateTypeUpdate(any(Storage.class), eq(invalidType));
        verify(storageRepo, never()).save(any());
        verify(storageMapper, never()).toDTO(any());
    }
}
