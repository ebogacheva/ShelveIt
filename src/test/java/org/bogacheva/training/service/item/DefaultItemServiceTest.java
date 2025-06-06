package org.bogacheva.training.service.item;

import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.repository.item.ItemRepository;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.ItemUpdateDTO;
import org.bogacheva.training.service.exceptions.InvalidItemOperationException;
import org.bogacheva.training.service.exceptions.ItemNotFoundException;
import org.bogacheva.training.service.exceptions.StorageNotFoundException;
import org.bogacheva.training.service.mapper.ItemMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultItemServiceTest {

    @Mock
    private ItemRepository itemRepo;

    @Mock
    private StorageRepository storageRepo;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private DefaultItemService itemService;

    @Test
    void create_whenValidInput_savesAndReturnsItemDTO() {

        Long storageId = 1L;
        Storage storage = new Storage();
        storage.setId(storageId);

        ItemCreateDTO createDTO = new ItemCreateDTO();
        createDTO.setName("Test Item");
        createDTO.setStorageId(storageId);

        Item newItem = new Item();
        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setStorage(storage);

        ItemDTO expectedDto = new ItemDTO();
        expectedDto.setId(1L);

        when(storageRepo.findById(storageId)).thenReturn(Optional.of(storage));
        when(itemMapper.toEntity(createDTO)).thenReturn(newItem);
        when(itemRepo.save(newItem)).thenReturn(savedItem);
        when(itemMapper.toDTO(savedItem)).thenReturn(expectedDto);

        ItemDTO result = itemService.create(createDTO);

        assertEquals(expectedDto.getId(), result.getId());
        verify(storageRepo).findById(storageId);
        verify(itemMapper).toEntity(createDTO);
        verify(itemRepo).save(newItem);
        verify(itemMapper).toDTO(savedItem);
    }

    @Test
    void getById_whenItemExists_returnsItemDTO() {

        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        ItemDTO expectedDto = new ItemDTO();
        expectedDto.setId(itemId);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toDTO(item)).thenReturn(expectedDto);

        ItemDTO result = itemService.getById(itemId);

        assertEquals(expectedDto.getId(), result.getId());
        verify(itemRepo).findById(itemId);
        verify(itemMapper).toDTO(item);
    }

    @Test
    void getById_whenItemDoesNotExist_throwsItemNotFoundException() {

        Long itemId = 1L;
        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getById(itemId));
        verify(itemRepo).findById(itemId);
    }

    @Test
    void getAll_whenItemsExist_returnsAllItemsAsDTOs() {

        List<Item> items = Arrays.asList(
                new Item("Item 1", new Storage()),
                new Item("Item 2", new Storage())
        );
        List<ItemDTO> expectedDtos = List.of(
                new ItemDTO(1L, "Item 1", null, null));

        when(itemRepo.findAll()).thenReturn(items);
        when(itemMapper.toDTOList(items)).thenReturn(expectedDtos);

        List<ItemDTO> result = itemService.getAll();

        assertEquals(expectedDtos, result);
        verify(itemRepo).findAll();
        verify(itemMapper).toDTOList(items);
    }

    @Test
    void delete_whenItemDoesNotExist_throwsNotFoundException() {

        Long itemId = 1L;
        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.delete(itemId));
        verify(itemRepo).findById(itemId);
        verify(itemRepo, never()).deleteById(itemId);
    }

    @Test
    void getByStorageId_whenStorageExists_returnsItems() {

        Long storageId = 1L;
        Storage storage = new Storage();
        storage.setId(storageId);

        List<Item> items = List.of(new Item("Item 1", storage));
        List<ItemDTO> expectedDtos = List.of(new ItemDTO(1L, "Item 1", null, null));

        when(storageRepo.findById(storageId)).thenReturn(Optional.of(storage));
        when(itemRepo.findItemsByStorageId(storageId)).thenReturn(items);
        when(itemMapper.toDTOList(items)).thenReturn(expectedDtos);

        List<ItemDTO> result = itemService.getByStorageId(storageId);

        assertEquals(expectedDtos, result);
        verify(storageRepo).findById(storageId);
        verify(itemRepo).findItemsByStorageId(storageId);
        verify(itemMapper).toDTOList(items);
    }

    @Test
    void getByStorageId_whenStorageDoesNotExist_throwsNotFoundException() {

        Long storageId = 1L;
        when(storageRepo.findById(storageId)).thenReturn(Optional.empty());

        assertThrows(StorageNotFoundException.class, () -> itemService.getByStorageId(storageId));
        verify(storageRepo).findById(storageId);
        verify(itemRepo, never()).findItemsByStorageId(anyLong());
    }

    @Test
    void findByKeywords_withValidKeywords_returnsMatchingItems() {

        List<String> keywords = Arrays.asList("key1", "key2");
        List<Item> items = List.of(new Item("Item 1", new Storage()));
        List<ItemDTO> expectedDtos = List.of(new ItemDTO(1L, "Item 1", null, null));

        when(itemRepo.findByAnyKeyword(keywords)).thenReturn(items);
        when(itemMapper.toDTOList(items)).thenReturn(expectedDtos);

        List<ItemDTO> result = itemService.findByKeywords(keywords);

        assertEquals(expectedDtos, result);
        verify(itemRepo).findByAnyKeyword(keywords);
        verify(itemMapper).toDTOList(items);
    }

    @Test
    void findByKeywords_withEmptyKeywords_returnsAllItems() {
        List<String> emptyKeywords = Collections.emptyList();
        List<Item> allItems = Arrays.asList(
                new Item("Item 1", new Storage()),
                new Item("Item 2", new Storage())
        );
        List<ItemDTO> expectedDtos = Arrays.asList(
                new ItemDTO(1L, "Item 1", null, null),
                new ItemDTO(2L, "Item 2", null, null)
        );

        when(itemRepo.findAll()).thenReturn(allItems);
        when(itemMapper.toDTOList(allItems)).thenReturn(expectedDtos);

        List<ItemDTO> result = itemService.findByKeywords(emptyKeywords);

        assertEquals(expectedDtos, result);
        verify(itemRepo).findAll();
        verify(itemMapper).toDTOList(allItems);
    }

    @Test
    void update_withValidData_updatesAndReturnsItem() {

        Long itemId = 1L;
        String newName = "Updated Item";
        List<String> newKeywords = Arrays.asList("new", "keywords");

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Old Name");

        Storage existingStorage = new Storage();
        existingStorage.setId(1L);
        existingItem.setStorage(existingStorage);

        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setName(newName);
        updateDTO.setKeywords(newKeywords);

        ItemDTO expected = new ItemDTO(itemId, newName, null, newKeywords);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepo.save(any(Item.class))).thenReturn(existingItem);
        when(itemMapper.toDTO(existingItem)).thenReturn(expected);

        ItemDTO result = itemService.update(itemId, updateDTO);

        assertEquals(expected, result);
        assertEquals(newName, existingItem.getName());
        assertEquals(newKeywords, existingItem.getKeywords());
        verify(itemRepo).findById(itemId);
        verify(itemRepo).save(existingItem);
        verify(itemMapper).toDTO(existingItem);
    }

    @Test
    void update_withStorageChange_updatesStorageAndReturnsItem() {

        Long itemId = 1L;
        Long oldStorageId = 1L;
        Long newStorageId = 2L;

        Storage oldStorage = new Storage();
        oldStorage.setId(oldStorageId);

        Storage newStorage = new Storage();
        newStorage.setId(newStorageId);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setStorage(oldStorage);

        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setStorageId(newStorageId);

        ItemDTO expectedDto = new ItemDTO();
        expectedDto.setId(itemId);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(storageRepo.findById(newStorageId)).thenReturn(Optional.of(newStorage));
        when(itemRepo.save(existingItem)).thenReturn(existingItem);
        when(itemMapper.toDTO(existingItem)).thenReturn(expectedDto);

        ItemDTO result = itemService.update(itemId, updateDTO);

        assertEquals(expectedDto, result);
        assertEquals(newStorage, existingItem.getStorage());
        verify(itemRepo).findById(itemId);
        verify(storageRepo).findById(newStorageId);
        verify(itemRepo).save(existingItem);
        verify(itemMapper).toDTO(existingItem);
    }

    @Test
    void update_withNullDTO_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> itemService.update(1L, null));
    }

    @Test
    void getItemsNear_whenItemHasStorage_returnsNearbyItems() {

        Long itemId = 1L;
        Long storageId = 5L;

        Storage storage = new Storage();
        storage.setId(storageId);

        Item item = new Item();
        item.setId(itemId);
        item.setStorage(storage);

        List<Item> nearbyItems = List.of(new Item("Nearby", storage));
        List<ItemDTO> expectedDtos = List.of(new ItemDTO(2L, "Nearby", null, null));

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepo.findItemsByStorageIdAndExcludeItemId(storageId, itemId)).thenReturn(nearbyItems);
        when(itemMapper.toDTOList(nearbyItems)).thenReturn(expectedDtos);

        List<ItemDTO> result = itemService.getItemsNear(itemId);

        assertEquals(expectedDtos, result);
        verify(itemRepo).findById(itemId);
        verify(itemRepo).findItemsByStorageIdAndExcludeItemId(storageId, itemId);
        verify(itemMapper).toDTOList(nearbyItems);
    }

    @Test
    void getItemsNear_whenItemHasNoStorage_throwsException() {

        Long itemId = 1L;

        Item item = new Item();
        item.setId(itemId);
        item.setStorage(null);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(InvalidItemOperationException.class, () -> itemService.getItemsNear(itemId));
        verify(itemRepo).findById(itemId);
    }

    @Test
    void getStorageHierarchyIds_whenItemHasStorage_returnsHierarchyIds() {

        Long itemId = 1L;
        Long storageId = 5L;

        Storage storage = new Storage();
        storage.setId(storageId);

        Item item = new Item();
        item.setId(itemId);
        item.setStorage(storage);

        List<Long> hierarchyIds = Arrays.asList(5L, 3L, 1L);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepo.findStorageHierarchyIds(itemId)).thenReturn(hierarchyIds);

        List<Long> result = itemService.getStorageHierarchyIds(itemId);

        assertEquals(hierarchyIds, result);
        verify(itemRepo).findById(itemId);
        verify(itemRepo).findStorageHierarchyIds(itemId);
    }

    @Test
    void getStorageHierarchyIds_whenItemHasNoStorage_throwsException() {

        Long itemId = 1L;

        Item item = new Item();
        item.setId(itemId);
        item.setStorage(null);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(InvalidItemOperationException.class, () -> itemService.getStorageHierarchyIds(itemId));
        verify(itemRepo).findById(itemId);
    }
}
