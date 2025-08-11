package org.bogacheva.training.service.item.unit;

import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.repository.item.ItemRepository;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.ItemUpdateDTO;
import org.bogacheva.training.service.exceptions.ItemNotFoundException;
import org.bogacheva.training.service.exceptions.StorageNotFoundException;
import org.bogacheva.training.service.item.crud.DefaultItemService;
import org.bogacheva.training.service.item.search.DefaultItemSearchService;
import org.bogacheva.training.service.mapper.ItemMapper;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Create item with valid input saves and returns ItemDTO")
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
    @DisplayName("Create item with keywords normalizes keywords to lowercase")
    void create_whenKeywordsPresent_normalizesToLowercase() {
        Long storageId = 1L;
        Storage storage = new Storage();
        storage.setId(storageId);

        ItemCreateDTO createDTO = new ItemCreateDTO();
        createDTO.setName("Test Item");
        createDTO.setStorageId(storageId);
        createDTO.setKeywords(List.of("KEY1", "Key2", "key3"));

        Item newItem = new Item();
        newItem.setKeywords(createDTO.getKeywords());

        ItemDTO expectedDto = new ItemDTO();
        expectedDto.setId(1L);

        when(storageRepo.findById(storageId)).thenReturn(Optional.of(storage));
        when(itemMapper.toEntity(createDTO)).thenReturn(newItem);
        when(itemRepo.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(itemMapper.toDTO(any(Item.class))).thenReturn(expectedDto);

        ItemDTO result = itemService.create(createDTO);

        assertEquals(expectedDto.getId(), result.getId());
        verify(itemRepo).save(argThat(item ->
                item.getKeywords() != null &&
                        item.getKeywords().equals(List.of("key1", "key2", "key3"))
        ));
    }

    @Test
    @DisplayName("Create item with null keywords does not fail and keeps keywords null")
    void create_whenKeywordsNull_doesNotFailAndKeepsNull() {
        Long storageId = 1L;
        Storage storage = new Storage();
        storage.setId(storageId);

        ItemCreateDTO createDTO = new ItemCreateDTO();
        createDTO.setName("Test Item");
        createDTO.setStorageId(storageId);
        createDTO.setKeywords(null);

        Item newItem = new Item();
        newItem.setKeywords(null);

        ItemDTO expectedDto = new ItemDTO();
        expectedDto.setId(1L);

        when(storageRepo.findById(storageId)).thenReturn(Optional.of(storage));
        when(itemMapper.toEntity(createDTO)).thenReturn(newItem);
        when(itemRepo.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(itemMapper.toDTO(any(Item.class))).thenReturn(expectedDto);

        ItemDTO result = itemService.create(createDTO);

        assertEquals(expectedDto.getId(), result.getId());
        verify(itemRepo).save(argThat(item -> item.getKeywords() == null));
    }

    @Test
    @DisplayName("Create item with empty keywords does not fail and keeps keywords empty")
    void create_whenKeywordsEmpty_doesNotFailAndKeepsEmptyList() {
        Long storageId = 1L;
        Storage storage = new Storage();
        storage.setId(storageId);

        ItemCreateDTO createDTO = new ItemCreateDTO();
        createDTO.setName("Test Item");
        createDTO.setStorageId(storageId);
        createDTO.setKeywords(Collections.emptyList());

        Item newItem = new Item();
        newItem.setKeywords(Collections.emptyList());

        ItemDTO expectedDto = new ItemDTO();
        expectedDto.setId(1L);

        when(storageRepo.findById(storageId)).thenReturn(Optional.of(storage));
        when(itemMapper.toEntity(createDTO)).thenReturn(newItem);
        when(itemRepo.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(itemMapper.toDTO(any(Item.class))).thenReturn(expectedDto);

        ItemDTO result = itemService.create(createDTO);

        assertEquals(expectedDto.getId(), result.getId());
        verify(itemRepo).save(argThat(item -> item.getKeywords() != null && item.getKeywords().isEmpty()));
    }

    @Test
    @DisplayName("Create item throws StorageNotFoundException if storage not found")
    void create_whenStorageNotFound_throwsException() {
        Long storageId = 99L;
        ItemCreateDTO createDTO = new ItemCreateDTO();
        createDTO.setName("Test Item");
        createDTO.setStorageId(storageId);

        when(storageRepo.findById(storageId)).thenReturn(Optional.empty());

        assertThrows(StorageNotFoundException.class, () -> itemService.create(createDTO));

        verify(storageRepo).findById(storageId);
        verifyNoMoreInteractions(itemMapper, itemRepo);
    }

    @Test
    @DisplayName("Create item throws IllegalArgumentException if storageId is null")
    void create_whenStorageIdNull_throwsException() {
        ItemCreateDTO createDTO = new ItemCreateDTO();
        createDTO.setName("Test");
        createDTO.setStorageId(null);

        assertThrows(IllegalArgumentException.class, () -> itemService.create(createDTO));
        verifyNoInteractions(storageRepo, itemRepo, itemMapper);
    }

    @Test
    @DisplayName("Create item throws NullPointerException if input DTO is null")
    void create_whenCreateDtoNull_throwsException() {
        assertThrows(NullPointerException.class, () -> itemService.create(null));
    }

    @Test
    @DisplayName("Get item by id returns ItemDTO when item exists")
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
    @DisplayName("Get item by id throws ItemNotFoundException if item does not exist")
    void getById_whenItemDoesNotExist_throwsItemNotFoundException() {
        Long itemId = 1L;
        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getById(itemId));
        verify(itemRepo).findById(itemId);
    }

    @Test
    @DisplayName("Get item by id throws IllegalArgumentException if id is null")
    void getById_whenIdNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> itemService.getById(null));
    }

    @Test
    @DisplayName("Get all items returns list of ItemDTOs")
    void getAll_whenItemsExist_returnsAllItemsAsDTOs() {
        List<Item> items = Arrays.asList(
                new Item("Item 1", new Storage()),
                new Item("Item 2", new Storage())
        );

        List<ItemDTO> expectedDtos = List.of(
                new ItemDTO(1L, "Item 1", null, null),
                new ItemDTO(2L, "Item 2", null, null)
        );

        when(itemRepo.findAll()).thenReturn(items);
        when(itemMapper.toDTOList(items)).thenReturn(expectedDtos);

        List<ItemDTO> result = itemService.getAll();

        assertEquals(expectedDtos, result);
        verify(itemRepo).findAll();
        verify(itemMapper).toDTOList(items);
    }

    @Test
    @DisplayName("Delete item deletes successfully when item exists")
    void delete_whenItemExists_deletesSuccessfully() {
        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        itemService.delete(itemId);

        verify(itemRepo).findById(itemId);
        verify(itemRepo).deleteById(itemId);
    }

    @Test
    @DisplayName("Delete item throws ItemNotFoundException if item does not exist")
    void delete_whenItemDoesNotExist_throwsItemNotFoundException() {
        Long itemId = 1L;
        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.delete(itemId));

        verify(itemRepo).findById(itemId);
        verify(itemRepo, never()).deleteById(itemId);
    }

    @Test
    @DisplayName("Delete item throws IllegalArgumentException if id is null")
    void delete_whenIdNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> itemService.delete(null));
    }

    @Test
    @DisplayName("Update item with valid data updates and returns updated ItemDTO")
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
    @DisplayName("Update item changes storage if new storage id provided")
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
    @DisplayName("Update item throws StorageNotFoundException if new storage does not exist")
    void update_whenStorageNotFound_throwsStorageNotFoundException() {
        Long itemId = 1L;
        Long newStorageId = 2L;

        Storage dummyStorage = new Storage();
        dummyStorage.setId(99L);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setStorage(dummyStorage);

        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setStorageId(newStorageId);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(storageRepo.findById(newStorageId)).thenReturn(Optional.empty());

        assertThrows(StorageNotFoundException.class, () -> itemService.update(itemId, updateDTO));

        verify(itemRepo).findById(itemId);
        verify(storageRepo).findById(newStorageId);
        verify(itemRepo, never()).save(any());
    }

    @Test
    @DisplayName("Update item throws IllegalArgumentException if name is empty")
    void update_whenNameEmpty_throwsIllegalArgumentException() {
        Long itemId = 1L;

        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setName("");

        assertThrows(IllegalArgumentException.class, () -> itemService.update(itemId, updateDTO));
    }

    @Test
    @DisplayName("Update item throws IllegalArgumentException if no fields provided")
    void update_whenNoFieldsProvided_throwsIllegalArgumentException() {
        Long itemId = 1L;
        ItemUpdateDTO updateDTO = new ItemUpdateDTO();

        assertThrows(IllegalArgumentException.class, () -> itemService.update(itemId, updateDTO));
    }

    @Test
    @DisplayName("Update item throws ItemNotFoundException if item does not exist")
    void update_whenItemNotFound_throwsItemNotFoundException() {
        Long itemId = 1L;
        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setName("Name");

        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.update(itemId, updateDTO));
        verify(itemRepo).findById(itemId);
    }
}