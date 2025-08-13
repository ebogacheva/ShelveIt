package org.bogacheva.training.service.item.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.bogacheva.training.ShelveItCommandLineRunner;
import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.repository.item.ItemRepository;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.exceptions.StorageNotFoundException;
import org.bogacheva.training.service.item.search.DefaultItemSearchService;
import org.bogacheva.training.service.mapper.ItemMapper;
import org.bogacheva.training.service.testdb.AbstractPostgresIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ItemSearchIntegrationTest extends AbstractPostgresIT {

    @MockitoBean
    private ShelveItCommandLineRunner commandLineRunner;

    @Autowired
    private DefaultItemSearchService itemSearchService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private ItemMapper itemMapper;

    private Storage rootStorage;
    private Storage childStorage;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        storageRepository.deleteAll();

        rootStorage = new Storage();
        rootStorage.setName("Root Storage");
        rootStorage = storageRepository.save(rootStorage);

        childStorage = new Storage();
        childStorage.setName("Child Storage");
        childStorage.setParent(rootStorage);
        childStorage = storageRepository.save(childStorage);

        item1 = new Item();
        item1.setName("Hammer");
        item1.setStorage(rootStorage);
        item1.setKeywords(Arrays.asList("tool", "heavy"));
        item1 = itemRepository.save(item1);

        item2 = new Item();
        item2.setName("Screwdriver");
        item2.setStorage(childStorage);
        item2.setKeywords(Arrays.asList("tool", "precision"));
        item2 = itemRepository.save(item2);

        item3 = new Item();
        item3.setName("Hand Saw");
        item3.setStorage(rootStorage);
        item3.setKeywords(Arrays.asList("cutting", "wood"));
        item3 = itemRepository.save(item3);
    }

    @Test
    @DisplayName("Search items by partial name case-insensitive")
    void testSearchItemsByName() {
        List<ItemDTO> results = itemSearchService.searchItemsByName("ham");
        assertThat(results).extracting("name").containsExactly("Hammer");

        results = itemSearchService.searchItemsByName("HAN");
        assertThat(results).extracting("name").containsExactly("Hand Saw");

        results = itemSearchService.searchItemsByName("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Search items by keywords case-insensitive partial match")
    void testSearchItemsByKeywords() {
        List<ItemDTO> results = itemSearchService.searchItemsByKeywords(List.of("tool"));
        assertThat(results).extracting("name").containsExactlyInAnyOrder("Hammer", "Screwdriver");

        results = itemSearchService.searchItemsByKeywords(List.of("heavy"));
        assertThat(results).extracting("name").containsExactly("Hammer");

        results = itemSearchService.searchItemsByKeywords(Arrays.asList("cutting", "precision"));
        assertThat(results).extracting("name").containsExactlyInAnyOrder("Screwdriver", "Hand Saw");

        results = itemSearchService.searchItemsByKeywords(List.of("nonexistent"));
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Search items by storage name includes sub-storages recursively")
    void testSearchItemsByStorageName() {
        List<ItemDTO> results = itemSearchService.searchItemsByStorageName("root");
        assertThat(results).extracting("name").containsExactlyInAnyOrder("Hammer", "Hand Saw", "Screwdriver");

        results = itemSearchService.searchItemsByStorageName("child");
        assertThat(results).extracting("name").containsExactly("Screwdriver");

        results = itemSearchService.searchItemsByStorageName("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Get items near a given item (same storage, exclude itself)")
    void testGetItemsNear() {
        List<ItemDTO> nearItems = itemSearchService.getItemsNear(item1.getId());
        assertThat(nearItems).extracting("name").containsExactly("Hand Saw");

        nearItems = itemSearchService.getItemsNear(item2.getId());
        assertThat(nearItems).isEmpty();
    }

    @Test
    @DisplayName("Get items by storage id")
    void testGetByStorageId() {
        List<ItemDTO> itemsInRoot = itemSearchService.getByStorageId(rootStorage.getId());
        assertThat(itemsInRoot).extracting("name").containsExactlyInAnyOrder("Hammer", "Hand Saw");

        List<ItemDTO> itemsInChild = itemSearchService.getByStorageId(childStorage.getId());
        assertThat(itemsInChild).extracting("name").containsExactly("Screwdriver");

        assertThrows(StorageNotFoundException.class, () -> itemSearchService.getByStorageId(999L));
    }

    @Test
    @DisplayName("Get storage hierarchy ids for an item")
    void testGetStorageHierarchyIds() {
        List<Long> hierarchyRoot = itemSearchService.getStorageHierarchyIds(item1.getId());
        assertThat(hierarchyRoot).containsExactly(rootStorage.getId());

        List<Long> hierarchyChild = itemSearchService.getStorageHierarchyIds(item2.getId());
        assertThat(hierarchyChild).contains(rootStorage.getId(), childStorage.getId());
    }

    @Test
    @DisplayName("Search with null or empty input returns empty list")
    void testSearchEmptyOrNullInput() {
        assertThat(itemSearchService.searchItemsByName(null)).isEmpty();
        assertThat(itemSearchService.searchItemsByName(" ")).isEmpty();

        assertThat(itemSearchService.searchItemsByKeywords(null)).isEmpty();
        assertThat(itemSearchService.searchItemsByKeywords(List.of())).isEmpty();

        assertThat(itemSearchService.searchItemsByStorageName(null)).isEmpty();
        assertThat(itemSearchService.searchItemsByStorageName(" ")).isEmpty();
    }
}
