package org.bogacheva.training.service.storage.unit;

import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.service.exceptions.StorageNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StorageServiceMoveContentsTest extends StorageServiceBaseTest {

    @Test
    void deleteAndMoveContents_shouldMoveContentsAndDelete_whenValidTargetProvided() {
        // Arrange
        Long sourceId = 1L;
        Long targetId = 2L;
        Storage home = createStorage(sourceId, "Home", StorageType.RESIDENCE, null);
        Storage unit = createStorage(5L, "Unit", StorageType.UNIT, home);
        Item item1 = createItem(3L, "Item 1", unit);
        Item item2 = createItem(4L, "Item 2", unit);
        List<Item> items = new ArrayList<>(List.of(item1, item2));
        unit.setItems(items);
        List<Storage> subStorages = new ArrayList<>(List.of(unit));
        home.setSubStorages(subStorages);

        Storage office = createStorage(targetId, "Office", StorageType.RESIDENCE, null);

        when(storageRepo.findById(sourceId)).thenReturn(Optional.of(home));
        when(storageRepo.findById(targetId)).thenReturn(Optional.of(office));

        // Act
        storageService.deleteAndMoveContents(sourceId, targetId);

        // Assert
        verify(storageRepo).findById(sourceId);
        verify(storageRepo).findById(targetId);
        verify(storageRepo).save(office);
        verify(storageRepo).delete(home);
    }

    @Test
    void deleteAndMoveContents_shouldUseParentAsTarget_whenNoTargetProvidedForNonResidence() {
        // Arrange
        Long sourceId = 2L;
        Long parentId = 1L;
        Storage home = createStorage(parentId, "home", StorageType.RESIDENCE, null);
        home.setItems(new ArrayList<>());
        home.setSubStorages(new ArrayList<>());

        // Create source storage with items
        Item sourceItem = createItem(1L, "Item 1", null);
        List<Item> sourceItems = new ArrayList<>(List.of(sourceItem));

        Storage sourceStorage = createStorage(sourceId, "Source", StorageType.ROOM, home);
        sourceStorage.setParent(home);
        sourceStorage.setItems(sourceItems);
        sourceStorage.setSubStorages(new ArrayList<>());

        // Add source to parent's sub-storages
        home.getSubStorages().add(sourceStorage);

        when(storageRepo.findById(sourceId)).thenReturn(Optional.of(sourceStorage));

        // Act
        storageService.deleteAndMoveContents(sourceId, null);

        // Assert
        verify(storageRepo).findById(sourceId);
        verify(storageRepo).save(home);
        verify(storageRepo).delete(sourceStorage);

        // Verify items were moved to parent
        assertEquals(1, home.getItems().size());
        assertTrue(home.getItems().contains(sourceItem));

        // Verify source storage lists were cleared
        assertEquals(0, sourceStorage.getItems().size());

        // Verify item now refers to parent
        assertEquals(home, sourceItem.getStorage());
    }

    @Test
    void deleteAndMoveContents_shouldThrowException_whenResidenceWithoutTargetProvided() {
        // Arrange
        Long residenceId = 1L;
        Storage residence = createStorage(residenceId, "Home", StorageType.RESIDENCE, null);

        when(storageRepo.findById(residenceId)).thenReturn(Optional.of(residence));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> storageService.deleteAndMoveContents(residenceId, null));

        assertEquals("RESIDENCE storage must specify a target storage for contents", exception.getMessage());

        verify(storageRepo).findById(residenceId);
        verify(storageRepo, never()).save(any());
        verify(storageRepo, never()).delete(any());
    }

    @Test
    void deleteAndMoveContents_shouldThrowException_whenSourceStorageNotFound() {
        // Arrange
        Long nonExistentId = 999L;
        Long targetId = 1L;

        when(storageRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        StorageNotFoundException exception = assertThrows(
                StorageNotFoundException.class,
                () -> storageService.deleteAndMoveContents(nonExistentId, targetId));

        assertEquals("Storage with ID: 999 was not found.", exception.getMessage());

        verify(storageRepo).findById(nonExistentId);
        verify(storageRepo, never()).save(any());
        verify(storageRepo, never()).delete(any());
    }

    @Test
    void deleteAndMoveContents_shouldThrowException_whenTargetStorageNotFound() {
        // Arrange
        Long sourceId = 1L;
        Long nonExistentTargetId = 999L;

        Storage source = createStorage(sourceId, "Source", StorageType.ROOM, null);

        when(storageRepo.findById(sourceId)).thenReturn(Optional.of(source));
        when(storageRepo.findById(nonExistentTargetId)).thenReturn(Optional.empty());

        // Act & Assert
        StorageNotFoundException exception = assertThrows(
                StorageNotFoundException.class,
                () -> storageService.deleteAndMoveContents(sourceId, nonExistentTargetId));

        assertEquals("Storage with ID: 999 was not found.", exception.getMessage());

        verify(storageRepo).findById(sourceId);
        verify(storageRepo).findById(nonExistentTargetId);
        verify(storageRepo, never()).save(any());
        verify(storageRepo, never()).delete(any());
    }
}
