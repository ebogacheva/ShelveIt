package org.bogacheva.training.service.item.unit;

import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.repository.item.ItemRepository;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.exceptions.InvalidItemOperationException;
import org.bogacheva.training.exceptions.ItemNotFoundException;
import org.bogacheva.training.exceptions.StorageNotFoundException;
import org.bogacheva.training.service.item.search.DefaultItemSearchService;
import org.bogacheva.training.service.mapper.ItemMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultItemSearchServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StorageRepository storageRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private DefaultItemSearchService itemSearchService;

    @DisplayName("search returns empty list when both name is null/blank/empty and keywords list is empty")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t"})
    void search_returnsEmptyList_whenInputBlankEmptyNameAndEmptyListOfKeywords(String name) {
        assertTrue(itemSearchService.search(name, List.of()).isEmpty());
    }

    @DisplayName("search returns empty list when keywords list is null or empty and name is null")
    @ParameterizedTest
    @NullAndEmptySource
    void search_returnsEmptyList_whenInputNullOrEmptyListKeywordsAndNullName(List<String> keywords) {
        assertTrue(itemSearchService.search(null, keywords).isEmpty());
    }

    @Test
    @DisplayName("search by name calls repository with lowercased pattern and maps results")
    void search_callsRepositoryAndMapsResults() {
        String input = "Test";
        String expectedPattern = "%test%";

        List<Item> foundItems = List.of(new Item(), new Item());
        List<ItemDTO> mappedDtos = List.of(new ItemDTO(), new ItemDTO());

        when(itemRepository.findByNameLikeIgnoreCase(expectedPattern)).thenReturn(foundItems);
        when(itemMapper.toDTOList(any())).thenReturn(mappedDtos);

        List<ItemDTO> result = itemSearchService.search(input, null);

        verify(itemRepository).findByNameLikeIgnoreCase(expectedPattern);
        verify(itemMapper).toDTOList(any());
        assertEquals(mappedDtos, result);
    }

    @ParameterizedTest(name = "searchItemsByKeywords with keywords={0} returns expected DTOs")
    @MethodSource("provideKeywordsAndExpectedResults")
    @DisplayName("searchItemsByKeywords returns expected results for various keyword inputs")
    void searchItemsByKeywords_returnsExpectedResults(List<String> searchKeywords,
                                                      List<Item> repoResult,
                                                      List<ItemDTO> expectedDtos) {

        List<String> lowerKeywords = searchKeywords == null ? null :
                searchKeywords.stream().map(String::toLowerCase).toList();

        if (lowerKeywords != null && !lowerKeywords.isEmpty()) {
            when(itemRepository.findByAnyKeyword(lowerKeywords)).thenReturn(repoResult);
        }

        if (repoResult != null) {
            when(itemMapper.toDTOList(anyList())).thenReturn(expectedDtos);
        }

        List<ItemDTO> result = itemSearchService.search(null, searchKeywords);

        assertEquals(expectedDtos, result);

        if (lowerKeywords != null && !lowerKeywords.isEmpty()) {
            verify(itemRepository).findByAnyKeyword(lowerKeywords);
            verify(itemMapper).toDTOList(anyList());
        } else {
            verify(itemRepository, never()).findByAnyKeyword(anyList());
            verify(itemMapper, never()).toDTOList(anyList());
        }
    }

    private static Stream<Arguments> provideKeywordsAndExpectedResults() {
        Item item1 = new Item("Item 1", new Storage());
        Item item2 = new Item("Item 2", new Storage());

        ItemDTO dto1 = new ItemDTO(1L, "Item 1", null, null);
        ItemDTO dto2 = new ItemDTO(2L, "Item 2", null, null);

        return Stream.of(
                // Case 1: Keywords match one item
                Arguments.of(List.of("key1"), List.of(item1), List.of(dto1)),

                // Case 2: Keywords match multiple items
                Arguments.of(List.of("key1", "key2"), List.of(item1, item2), List.of(dto1, dto2)),

                // Case 3: Keywords don't match any item
                Arguments.of(List.of("nomatch"), Collections.emptyList(), Collections.emptyList()),

                // Case 4: Empty keywords list
                Arguments.of(Collections.emptyList(), null, Collections.emptyList()),

                // Case 5: Null keywords
                Arguments.of(null, null, Collections.emptyList())
        );
    }

    @Test
    @DisplayName("searchItemsByStorageName collects items from all matched storages excluding sub-storages")
    void searchItemsByStorageName_collectsItemsFromAllMatchedStorages() {
        String input = "storage";
        String expectedPattern = "%storage%";

        Storage storage1 = new Storage();
        Storage storage2 = new Storage();
        Storage subStorage = new Storage();

        Item item1 = new Item();
        Item item2 = new Item();
        Item subItem = new Item();

        storage1.setItems(List.of(item1));
        storage2.setItems(List.of(item2));
        storage2.setSubStorages(List.of(subStorage));
        subStorage.setItems(List.of(subItem));
        subStorage.setSubStorages(Collections.emptyList());

        when(storageRepository.findByNameLikeIgnoreCase(expectedPattern))
                .thenReturn(List.of(storage1, storage2));

        List<Item> allItems = List.of(item1, item2, subItem);
        List<ItemDTO> mappedDtos = List.of(new ItemDTO(), new ItemDTO(), new ItemDTO());

        when(itemMapper.toDTOList(anyList())).thenReturn(mappedDtos);

        List<ItemDTO> result = itemSearchService.searchItemsByStorageName(input);

        verify(storageRepository).findByNameLikeIgnoreCase(expectedPattern);
        verify(itemMapper).toDTOList(anyList());
        assertEquals(mappedDtos, result);
    }

    @Test
    @DisplayName("getItemsNear throws ItemNotFoundException when item not found")
    void getItemsNear_throwsException_whenItemNotFound() {
        Long itemId = 10L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemSearchService.getItemsNear(itemId));
    }

    @Test
    @DisplayName("getItemsNear throws InvalidItemOperationException when storage is null")
    void getItemsNear_throwsException_whenStorageIsNull() {
        Long itemId = 10L;
        Item item = new Item();
        item.setStorage(null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        InvalidItemOperationException ex = assertThrows(InvalidItemOperationException.class,
                () -> itemSearchService.getItemsNear(itemId));
        assertTrue(ex.getMessage().contains("has no associated storage"));
    }

    @Test
    @DisplayName("getItemsNear returns mapped items when storage exists")
    void getItemsNear_returnsMappedItems_whenStorageExists() {
        Long itemId = 10L;
        Storage storage = new Storage();
        storage.setId(99L);

        Item item = new Item();
        item.setStorage(storage);

        List<Item> nearItems = List.of(new Item(), new Item());
        List<ItemDTO> mappedDtos = List.of(new ItemDTO(), new ItemDTO());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.findItemsByStorageIdAndExcludeItemId(storage.getId(), itemId)).thenReturn(nearItems);
        when(itemMapper.toDTOList(nearItems)).thenReturn(mappedDtos);

        List<ItemDTO> result = itemSearchService.getItemsNear(itemId);

        verify(itemRepository).findById(itemId);
        verify(itemRepository).findItemsByStorageIdAndExcludeItemId(storage.getId(), itemId);
        verify(itemMapper).toDTOList(nearItems);
        assertEquals(mappedDtos, result);
    }

    @Test
    @DisplayName("getByStorageId throws StorageNotFoundException when storage not found")
    void getByStorageId_throwsException_whenStorageNotFound() {
        when(storageRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(StorageNotFoundException.class, () -> itemSearchService.getByStorageId(anyLong()));
    }

    @Test
    @DisplayName("getByStorageId returns mapped items")
    void getByStorageId_returnsMappedItems() {
        Long storageId = 20L;
        Storage storage = new Storage();
        storage.setId(storageId);

        List<Item> items = List.of(new Item());
        List<ItemDTO> dtos = List.of(new ItemDTO());

        when(storageRepository.findById(storageId)).thenReturn(Optional.of(storage));
        when(itemRepository.findItemsByStorageId(storageId)).thenReturn(items);
        when(itemMapper.toDTOList(items)).thenReturn(dtos);

        List<ItemDTO> result = itemSearchService.getByStorageId(storageId);

        verify(storageRepository).findById(storageId);
        verify(itemRepository).findItemsByStorageId(storageId);
        verify(itemMapper).toDTOList(items);
        assertEquals(dtos, result);
    }

    @Test
    @DisplayName("getStorageHierarchyIds throws ItemNotFoundException when item not found")
    void getStorageHierarchyIds_throwsException_whenItemNotFound() {
        Long itemId = 15L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemSearchService.getStorageHierarchyIds(itemId));
    }

    @Test
    @DisplayName("getStorageHierarchyIds throws InvalidItemOperationException when storage is null")
    void getStorageHierarchyIds_throwsException_whenStorageIsNull() {
        Long itemId = 15L;
        Item item = new Item();
        item.setStorage(null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(InvalidItemOperationException.class, () -> itemSearchService.getStorageHierarchyIds(itemId));
    }

    @Test
    @DisplayName("getStorageHierarchyIds returns list of storage hierarchy IDs when storage exists")
    void getStorageHierarchyIds_returnsIds_whenStorageExists() {
        Long itemId = 15L;
        Storage storage = new Storage();
        storage.setId(100L);
        Item item = new Item();
        item.setStorage(storage);

        List<Long> hierarchyIds = List.of(1L, 2L, 3L);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.findStorageHierarchyIds(itemId)).thenReturn(hierarchyIds);

        List<Long> result = itemSearchService.getStorageHierarchyIds(itemId);

        verify(itemRepository).findById(itemId);
        verify(itemRepository).findStorageHierarchyIds(itemId);
        assertEquals(hierarchyIds, result);
    }
}
