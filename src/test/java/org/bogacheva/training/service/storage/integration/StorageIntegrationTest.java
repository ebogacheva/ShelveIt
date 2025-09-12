package org.bogacheva.training.service.storage.integration;

import org.bogacheva.training.ShelveItCommandLineRunner;
import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.repository.item.ItemRepository;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.dto.StorageUpdateDTO;
import org.bogacheva.training.service.storage.StorageService;
import org.bogacheva.training.service.testdb.AbstractPostgresIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class StorageIntegrationTest extends AbstractPostgresIT {

    @MockitoBean
    private ShelveItCommandLineRunner commandLineRunner;

    @Autowired
    private StorageService storageService;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Storage kitchen, fridge, home;
    private Item milk, bread;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        storageRepository.deleteAll();

        home = storageRepository.save(new Storage("Home", StorageType.RESIDENCE, null));
        Storage room = new Storage("Living Room", StorageType.ROOM, home);
        Storage bedroom = new Storage("Bedroom", StorageType.ROOM, home);
        kitchen = new Storage("Kitchen", StorageType.ROOM, home);
        fridge = new Storage("Fridge", StorageType.FURNITURE, kitchen);
        storageRepository.saveAll(List.of(room, bedroom, kitchen, fridge));

        milk = itemRepository.save(new Item("Milk", fridge));
        bread = itemRepository.save(new Item("Bread", fridge));
    }

    @Test
    void getAll_shouldReturnAllStorages() {
        List<StorageDTO> all = storageService.getAll(null);
        assertThat(all)
                .hasSize(5)
                .extracting(StorageDTO::getName)
                .containsExactlyInAnyOrder("Living Room", "Bedroom", "Fridge", "Kitchen", "Home");
    }

    @Test
    void getSubStorages_shouldReturnImmediateChildren() {
        List<StorageDTO> subStorages = storageService.getSubStorages(home.getId());

        assertThat(subStorages)
                .hasSize(3)
                .extracting(StorageDTO::getName)
                .containsExactlyInAnyOrder("Living Room", "Bedroom", "Kitchen");
    }

    @Test
    void getAllItemDTOs_shouldReturnAllItemsRecursively() {
        List<ItemDTO> allItems = storageService.getAllItemDTOs(home.getId());

        assertThat(allItems)
                .hasSize(2)
                .extracting(ItemDTO::getName)
                .containsExactlyInAnyOrder("Milk", "Bread");
    }

    @Test
    void addItems_shouldAssociateItemsWithStorage() {

        StorageDTO garage = storageService.create(new StorageCreateDTO("Garage", StorageType.ROOM, home.getId()));


        Item hammer = itemRepository.save(new Item("Car #1", home));
        Item wrench = itemRepository.save(new Item("Car #2", home));

        StorageDTO updated = storageService.addItems(garage.getId(), List.of(hammer.getId(), wrench.getId()));

        assertThat(updated.getItems()).hasSize(2);
        assertThat(itemRepository.findById(hammer.getId()).get().getStorage().getId())
                .isEqualTo(garage.getId());
        assertThat(itemRepository.findById(wrench.getId()).get().getStorage().getId())
                .isEqualTo(garage.getId());
    }

    @Test
    void removeItems_shouldUnlinkItemsFromStorage() {
        StorageDTO updated = storageService.removeItems(fridge.getId(), List.of(milk.getId()));

        assertThat(updated.getItems())
                .doesNotContain(milk.getId())
                .contains(bread.getId());

        assertThat(itemRepository.findById(milk.getId()).isEmpty());
    }

    @Test
    void searchByNameAndType_shouldFilterByPartialNameAndType() {
        List<StorageDTO> result = storageService.searchByNameAndType("room", StorageType.ROOM);

        assertThat(result)
                .hasSize(2)
                .extracting(StorageDTO::getName)
                .containsExactlyInAnyOrder("Living Room", "Bedroom");
    }

    @Test
    void create_shouldPersistAndReturnNewStorage() {
        StorageCreateDTO dto = new StorageCreateDTO();
        dto.setName("Garage");
        dto.setType(StorageType.ROOM);
        dto.setParentId(home.getId());

        StorageDTO created = storageService.create(dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Garage");

        assertThat(storageRepository.findById(created.getId())).isPresent();
    }

    @Test
    void update_shouldModifyExistingStorage() {

        StorageUpdateDTO updateDTO = new StorageUpdateDTO();
        updateDTO.setName("Kitchen Fridge");

        StorageDTO updated = storageService.update(fridge.getId(), updateDTO);

        assertThat(updated.getName()).isEqualTo("Kitchen Fridge");
    }

    @Test
    void delete_shouldRemoveStorage() {
        storageService.delete(fridge.getId());

        assertThat(storageRepository.findById(fridge.getId())).isEmpty();
        assertThat(itemRepository.findAll()).isEmpty();
    }
}
