package org.bogacheva.training.service.item.integration;

import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.repository.item.ItemRepository;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.ItemUpdateDTO;
import org.bogacheva.training.exceptions.ItemNotFoundException;
import org.bogacheva.training.service.item.crud.ItemService;
import org.bogacheva.training.service.testdb.AbstractPostgresIT;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class ItemIntegrationTest extends AbstractPostgresIT {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private StorageRepository storageRepo;

    private Storage testStorage;
    private Storage secondStorage;

    private ItemCreateDTO validCreateDTO;

    @BeforeEach
    void setup() {
        itemRepo.deleteAll();
        storageRepo.deleteAll();

        testStorage = new Storage("Test Residence", StorageType.RESIDENCE, null);
        testStorage = storageRepo.save(testStorage);

        secondStorage = new Storage("Test Room", StorageType.ROOM, testStorage);
        secondStorage = storageRepo.save(secondStorage);

        validCreateDTO = new ItemCreateDTO();
        validCreateDTO.setName("Integration Test Item");
        validCreateDTO.setStorageId(testStorage.getId());
        validCreateDTO.setKeywords(Arrays.asList("test", "integration"));
    }

    @Test
    @DisplayName("CreateItem returns correct DTO")
    void createItem_persistsAndReturnsCorrectDTO() {
        ItemDTO created = itemService.create(validCreateDTO);

        assertNotNull(created.getId());
        assertEquals(validCreateDTO.getName(), created.getName());
        assertEquals(testStorage.getId(), created.getStorage().getId());
        assertEquals(2, created.getKeywords().size());
        assertTrue(created.getKeywords().contains("test"));
        assertTrue(created.getKeywords().contains("integration"));

        Optional<Item> fromDb = itemRepo.findById(created.getId());
        assertTrue(fromDb.isPresent());
        assertEquals(validCreateDTO.getName(), fromDb.get().getName());
    }

    @Test
    @DisplayName("Get item by ID returns correct item")
    void getById_returnsCorrectItem() {
        ItemDTO created = itemService.create(validCreateDTO);
        ItemDTO fetched = itemService.getById(created.getId());

        assertEquals(created.getId(), fetched.getId());
        assertEquals(created.getName(), fetched.getName());
        assertEquals(created.getStorage().getId(), fetched.getStorage().getId());
    }

    @Test
    @DisplayName("Get by non-existent item ID throws ItemNotFoundException")
    void getById_nonExistent_throwsException() {
        Long nonExistentId = 9999L;
        assertThrows(ItemNotFoundException.class, () -> itemService.getById(nonExistentId));
    }

    @Test
    @DisplayName("GetAll returns all items")
    void getAll_returnsAllItems() {
        itemService.create(validCreateDTO);
        validCreateDTO.setName("Second Item");
        itemService.create(validCreateDTO);

        List<ItemDTO> allItems = itemService.getAll();

        assertEquals(2, allItems.size());
    }

    @Test
    @DisplayName("Update item changes name and keywords attributes")
    void updateItem_changesAttributes() {
        ItemDTO created = itemService.create(validCreateDTO);

        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setKeywords(Arrays.asList("updated", "keywords"));

        ItemDTO updated = itemService.update(created.getId(), updateDTO);

        assertEquals("Updated Name", updated.getName());
        assertEquals(2, updated.getKeywords().size());
        assertTrue(updated.getKeywords().contains("updated"));

        Optional<Item> fromDb = itemRepo.findById(created.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("Updated Name", fromDb.get().getName());
    }

    @Test
    @DisplayName("Update item changes storage")
    void updateItem_changesStorage() {
        ItemDTO created = itemService.create(validCreateDTO);

        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setStorageId(secondStorage.getId());

        ItemDTO updated = itemService.update(created.getId(), updateDTO);

        assertEquals(secondStorage.getId(), updated.getStorage().getId());

        Optional<Item> fromDb = itemRepo.findById(created.getId());
        assertTrue(fromDb.isPresent());
        assertEquals(secondStorage.getId(), fromDb.get().getStorage().getId());
    }

    @Test
    @DisplayName("Delete removes item from database")
    void deleteItem_removesFromDatabase() {
        ItemDTO created = itemService.create(validCreateDTO);
        Long id = created.getId();

        assertTrue(itemRepo.existsById(id));

        itemService.delete(id);

        assertFalse(itemRepo.existsById(id));
    }

    @Test
    @DisplayName("Create item with null storage ID throws IllegalArgumentException")
    void createItem_withNullStorageId_throwsException() {
        validCreateDTO.setStorageId(null);
        assertThrows(IllegalArgumentException.class, () -> itemService.create(validCreateDTO));
    }

    @Test
    @DisplayName("Update item with empty update DTO throws IllegalArgumentException")
    void updateItem_withEmptyUpdateDTO_throwsException() {
        ItemDTO created = itemService.create(validCreateDTO);
        assertThrows(IllegalArgumentException.class, () -> itemService.update(created.getId(), new ItemUpdateDTO()));
    }

    @Test
    @DisplayName("Update item with empty name throws IllegalArgumentException")
    void updateItem_withEmptyName_throwsException() {
        ItemDTO created = itemService.create(validCreateDTO);

        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setName("   ");

        assertThrows(IllegalArgumentException.class, () -> itemService.update(created.getId(), updateDTO));
    }
}
