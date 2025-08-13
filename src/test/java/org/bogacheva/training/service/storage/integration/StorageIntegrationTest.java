package org.bogacheva.training.service.storage.integration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.transaction.Transactional;
import org.bogacheva.training.ShelveItCommandLineRunner;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.dto.StorageUpdateDTO;
import org.bogacheva.training.service.mapper.StorageMapper;
import org.bogacheva.training.service.storage.StorageService;
import org.bogacheva.training.service.testdb.AbstractPostgresIT;
import org.junit.jupiter.api.BeforeEach;
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

    private Storage home;

    @BeforeEach
    void setUp() {
        storageRepository.deleteAll();
        home = storageRepository.save(new Storage("Home", StorageType.RESIDENCE, null));
        Storage room = new Storage("Living Room", StorageType.ROOM, home);
        Storage bedroom = new Storage("Bedroom", StorageType.ROOM, home);
        Storage kitchen = new Storage("Kitchen", StorageType.ROOM, home);
        Storage fridge = new Storage("Fridge", StorageType.FURNITURE, kitchen);
        storageRepository.saveAll(List.of(room, bedroom, kitchen, fridge));
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
        StorageDTO storage = storageService.searchByNameAndType("Fridge", null).get(0);

        StorageUpdateDTO updateDTO = new StorageUpdateDTO();
        updateDTO.setName("Kitchen Fridge");

        StorageDTO updated = storageService.update(storage.getId(), updateDTO);

        assertThat(updated.getName()).isEqualTo("Kitchen Fridge");
    }

    @Test
    void delete_shouldRemoveStorage() {
        StorageDTO storage = storageService.searchByNameAndType("Fridge", null).get(0);

        storageService.delete(storage.getId());

        assertThat(storageRepository.findById(storage.getId())).isEmpty();
    }
}
