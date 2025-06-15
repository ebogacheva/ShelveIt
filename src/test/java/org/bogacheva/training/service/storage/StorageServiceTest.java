package org.bogacheva.training.service.storage;

import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.exceptions.InvalidStorageHierarchyException;
import org.bogacheva.training.service.exceptions.StorageNotFoundException;
import org.bogacheva.training.service.mapper.StorageMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private StorageMapper storageMapper;

    @Mock
    private StorageRepository storageRepo;

    @InjectMocks
    private DefaultStorageService storageService;

    @Mock
    private StorageValidator validator;

    @Test
    void create_shouldCallStorageRepo_WhenValidResidenceDTOProvided() {
        StorageCreateDTO residenceDTO = new StorageCreateDTO("Storage Name", StorageType.RESIDENCE, null);
        Storage storage = new Storage("Storage Name", StorageType.ROOM, null);

        doNothing().when(validator).validateStorageCreation(residenceDTO);
        when(storageMapper.toEntity(residenceDTO)).thenReturn(storage);
        when(storageRepo.save(storage)).thenReturn(storage);

        storageService.create(residenceDTO);

        verify(validator).validateStorageCreation(residenceDTO);
        verify(storageMapper).toEntity(residenceDTO);
        verify(storageRepo).save(storage);
    }

    @Test
    void create_shouldThrowException_WhenRoomDTOWithoutParentProvided() {
        StorageCreateDTO roomDTO = new StorageCreateDTO("Storage Name", StorageType.ROOM, null);

        doThrow(new InvalidStorageHierarchyException("Storage type ROOM requires a parent"))
                .when(validator).validateStorageCreation(roomDTO);

        assertThrows(InvalidStorageHierarchyException.class, () -> storageService.create(roomDTO));

        verify(validator).validateStorageCreation(roomDTO);
        verify(storageMapper, never()).toEntity(any());
        verify(storageRepo, never()).save(any());
    }

    @Test
    void create_shouldThrowException_WhenResidenceDTOWithParentProvided() {
        StorageCreateDTO residenceDTO = new StorageCreateDTO("Storage Name", StorageType.RESIDENCE, 1L);
        doThrow(new InvalidStorageHierarchyException("RESIDENCE storage should not have a parent"))
                .when(validator).validateStorageCreation(residenceDTO);

        assertThrows(InvalidStorageHierarchyException.class, () -> storageService.create(residenceDTO));

        verify(validator).validateStorageCreation(residenceDTO);
        verify(storageMapper, never()).toEntity(any());
        verify(storageRepo, never()).save(any());
    }

    @Test
    void findById_shouldReturnStorageDTO_WhenValidIdProvided() {
        Storage storage = createStorage(1L, "Storage Name", StorageType.RESIDENCE, null);
        StorageDTO expectedResult = createStorageDTO(1L, "Storage Name", StorageType.RESIDENCE, null);

        when(storageRepo.findById(1L)).thenReturn(Optional.of(storage));
        when(storageMapper.toDTO(storage)).thenReturn(expectedResult);

        StorageDTO actualResult = storageService.getById(1L);

        assertNotNull(actualResult);
        assertThat(actualResult)
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
        verify(storageRepo, times(1)).findById(1L);
        verify(storageMapper, times(1)).toDTO(storage);
    }

    @Test
    void findById_shouldThrowStorageNotFound_WhenInvalidIdProvided() {
        when(storageRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(StorageNotFoundException.class, () -> storageService.getById(999L));
        verify(storageRepo, times(1)).findById(999L);
    }

    @Test
    void findAll_shouldReturnListOfStorages() {
        Storage storage1 = createStorage(1L, "Storage One", StorageType.RESIDENCE, null);
        Storage storage2 = createStorage(2L, "Storage Two", StorageType.ROOM, storage1);

        StorageDTO storageDTO1 = createStorageDTO(1L, "Storage One", StorageType.RESIDENCE, null);
        StorageDTO storageDTO2 = createStorageDTO(2L, "Storage Two", StorageType.ROOM, 1L);

        when(storageRepo.findAll()).thenReturn(List.of(storage1, storage2));
        when(storageMapper.toDTOList(anyList())).thenReturn(List.of(storageDTO1, storageDTO2));

        List<StorageDTO> result = storageService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(storageRepo, times(1)).findAll();
        verify(storageMapper, times(1)).toDTOList(anyList());
    }

    private Storage createStorage(Long id, String name, StorageType type, Storage parent) {
        Storage storage = new Storage(name, type, parent);
        storage.setId(id);
        return storage;
    }

    private StorageDTO createStorageDTO(Long id, String name, StorageType type, Long parentId) {
        return new StorageDTO(id, name, type, new ArrayList<>(), new ArrayList<>(), parentId);
    }
}