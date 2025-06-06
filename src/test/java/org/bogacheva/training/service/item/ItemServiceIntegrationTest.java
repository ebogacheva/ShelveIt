package org.bogacheva.training.service.item;

import org.bogacheva.training.ShelveItCommandLineRunner;
import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.repository.item.ItemRepository;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.ItemUpdateDTO;
import org.bogacheva.training.service.exceptions.ItemNotFoundException;
import org.bogacheva.training.service.exceptions.StorageNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class ItemServiceIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @MockitoBean
    private ShelveItCommandLineRunner commandLineRunner;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private StorageRepository storageRepo;

    private Storage testStorage;
    private Storage secondStorage;
    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    void setUp() {
        itemRepo.deleteAll();
        storageRepo.deleteAll();

        testStorage = new Storage("Test Residence", StorageType.RESIDENCE, null);
        testStorage = storageRepo.save(testStorage);

        secondStorage = new Storage("Test Room", StorageType.ROOM, testStorage);
        secondStorage = storageRepo.save(secondStorage);

        testItem1 = new Item("Existing Item", testStorage);
        testItem1.setKeywords(new ArrayList<>(Arrays.asList("keyword1", "common")));
        testItem1 = itemRepo.save(testItem1);

        testItem2 = new Item("Another Item", secondStorage);
        testItem2.setKeywords(new ArrayList<>(Arrays.asList("keyword2", "common")));
        testItem2 = itemRepo.save(testItem2);
    }

    @Test
    @Transactional
    void createItem_persistsToDatabase_andReturnsCorrectDTO() {
        ItemCreateDTO createDTO = new ItemCreateDTO();
        createDTO.setName("Integration Test Item");
        createDTO.setStorageId(testStorage.getId());
        createDTO.setKeywords(Arrays.asList("test", "integration"));

        ItemDTO result = itemService.create(createDTO);

        assertNotNull(result.getId());
        assertEquals("Integration Test Item", result.getName());
        assertEquals(testStorage.getId(), result.getStorage().getId());
        assertEquals(2, result.getKeywords().size());
        assertTrue(result.getKeywords().contains("test"));
        assertTrue(result.getKeywords().contains("integration"));

        Optional<Item> fromDb = itemRepo.findById(result.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("Integration Test Item", fromDb.get().getName());
        assertEquals(testStorage.getId(), fromDb.get().getStorage().getId());
    }

    @Test
    @Transactional
    void getById_returnsCorrectItem() {
        ItemDTO result = itemService.getById(testItem1.getId());

        assertNotNull(result);
        assertEquals(testItem1.getId(), result.getId());
        assertEquals(testItem1.getName(), result.getName());
        assertEquals(testItem1.getStorage().getId(), result.getStorage().getId());
        assertEquals(testItem1.getKeywords().size(), result.getKeywords().size());
    }

    @Test
    @Transactional
    void getById_withNonExistentId_throwsException() {
        Long nonExistentId = 999L;
        Exception exception = assertThrows(ItemNotFoundException.class, () -> {
            itemService.getById(nonExistentId);
        });

        assertTrue(exception.getMessage().contains(String.valueOf(nonExistentId)));
    }

    @Test
    @Transactional
    void getAll_returnsAllItems() {
        List<ItemDTO> results = itemService.getAll();

        assertEquals(2, results.size());
        Set<Long> itemIds = results.stream().map(ItemDTO::getId).collect(Collectors.toSet());
        assertTrue(itemIds.contains(testItem1.getId()));
        assertTrue(itemIds.contains(testItem2.getId()));
    }

    @Test
    @Transactional
    void delete_removesItemFromDatabase() {
        Long itemId = testItem1.getId();
        assertTrue(itemRepo.existsById(itemId));

        itemService.delete(itemId);

        assertFalse(itemRepo.existsById(itemId));
    }

    @Test
    @Transactional
    void getByStorageId_returnsItemsInSpecificStorage() {
        List<ItemDTO> results = itemService.getByStorageId(testStorage.getId());

        assertEquals(1, results.size());
        assertEquals(testItem1.getId(), results.get(0).getId());
    }

    @Test
    @Transactional
    void getByStorageId_withNonExistentStorageId_throwsException() {
        Long nonExistentId = 999L;
        Exception exception = assertThrows(StorageNotFoundException.class, () -> {
            itemService.getByStorageId(nonExistentId);
        });

        assertTrue(exception.getMessage().contains(String.valueOf(nonExistentId)));
    }

    @Test
    @Transactional
    void findByKeywords_returnsMatchingItems() {
        List<ItemDTO> results = itemService.findByKeywords(Collections.singletonList("common"));

        assertEquals(2, results.size());
        Set<Long> itemIds = results.stream().map(ItemDTO::getId).collect(Collectors.toSet());
        assertTrue(itemIds.contains(testItem1.getId()));
        assertTrue(itemIds.contains(testItem2.getId()));

        results = itemService.findByKeywords(Collections.singletonList("keyword1"));

        assertEquals(1, results.size());
        assertEquals(testItem1.getId(), results.get(0).getId());
    }

    @Test
    @Transactional
    void findByKeywords_withEmptyList_returnsAllItems() {
        List<ItemDTO> results = itemService.findByKeywords(Collections.emptyList());

        assertEquals(2, results.size());
    }

    @Test
    @Transactional
    void update_changesItemAttributes() {
        Long itemId = testItem1.getId();
        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setName("Updated Name");

        List<String> keywords = new ArrayList<>();
        keywords.add("updated");
        keywords.add("keywords");
        updateDTO.setKeywords(keywords);

        ItemDTO result = itemService.update(itemId, updateDTO);

        assertEquals("Updated Name", result.getName());
        assertEquals(2, result.getKeywords().size());
        assertTrue(result.getKeywords().contains("updated"));

        Optional<Item> fromDb = itemRepo.findById(itemId);
        assertTrue(fromDb.isPresent());
        assertEquals("Updated Name", fromDb.get().getName());
        assertEquals(2, fromDb.get().getKeywords().size());
    }

    @Test
    @Transactional
    void update_changesItemStorage() {
        Long itemId = testItem1.getId();
        Long originalStorageId = testItem1.getStorage().getId();
        Long newStorageId = secondStorage.getId();

        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setStorageId(newStorageId);

        ItemDTO result = itemService.update(itemId, updateDTO);

        assertNotEquals(originalStorageId, result.getStorage().getId());
        assertEquals(newStorageId, result.getStorage().getId());

        Optional<Item> fromDb = itemRepo.findById(itemId);
        assertTrue(fromDb.isPresent());
        assertEquals(newStorageId, fromDb.get().getStorage().getId());
    }

    @Test
    @Transactional
    void getItemsNear_returnsItemsInSameStorage() {
        Item sameStorageItem = new Item("Same Storage Item", testStorage);
        sameStorageItem = itemRepo.save(sameStorageItem);

        List<ItemDTO> results = itemService.getItemsNear(testItem1.getId());

        assertEquals(1, results.size());
        assertEquals(sameStorageItem.getId(), results.get(0).getId());
    }

    @Test
    @Transactional
    void getStorageHierarchyIds_returnsCorrectHierarchy() {
        List<Long> results = itemService.getStorageHierarchyIds(testItem2.getId());

        assertEquals(2, results.size());
        assertTrue(results.contains(secondStorage.getId()));
        assertTrue(results.contains(testStorage.getId()));
    }
}
