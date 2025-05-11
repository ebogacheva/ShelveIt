package org.bogacheva.training.service.storage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.mapper.StorageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.profiles.active=test")
public class StorageServiceIntegrationTest {

    @Autowired
    private StorageService storageService;

    @Autowired
    private StorageRepository storageRepo;

    @Autowired
    private StorageMapper storageMapper;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @Transactional
    public void setUp() {
        resetAutoIncrement();
    }

    @Transactional
    public void resetAutoIncrement() {
        Query queryStorage = entityManager.createNativeQuery("TRUNCATE TABLE storages RESTART IDENTITY");
        queryStorage.executeUpdate();

        Query queryItems = entityManager.createNativeQuery("TRUNCATE TABLE items RESTART IDENTITY");
        queryItems.executeUpdate();
    }

    @Test
    void shouldReturnStorageDTOWithExpectedValues_WhenValidResidenceDTOIsProvided() {
        StorageCreateDTO residenceDTO = new StorageCreateDTO("Storage Name", StorageType.RESIDENCE, null);

        StorageDTO actual = storageService.create(residenceDTO);

        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("Storage Name");
        assertThat(actual.getType()).isEqualTo(StorageType.RESIDENCE);
        assertThat(actual.getParentId()).isNull();
        assertThat(actual.getStorages()).isEmpty();
        assertThat(actual.getItems()).isEmpty();
    }


    @Transactional
    @Test
    void getById_shouldReturnExpectedStorageDTO_whenValidIdProvided() {
        Storage storage = new Storage("Kate and Alex house", StorageType.RESIDENCE, null);
        entityManager.persist(storage);
        Long id = storage.getId();
        StorageDTO actual = storageService.getById(id);

        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("Kate and Alex house");
        assertThat(actual.getType()).isEqualTo(StorageType.RESIDENCE);
        assertThat(actual.getParentId()).isNull();
    }

    @Transactional
    @Test
    void getAll_shouldReturnListOfStorageDTOs() {
        createTestStorages();

        List<StorageDTO> actual = storageService.getAll();
        assertThat(actual).isNotEmpty();

        assertThat(actual).anyMatch(storageDTO ->
                "Kate and Alex house".equals(storageDTO.getName()) &&
                        StorageType.RESIDENCE == storageDTO.getType()
        );
        assertThat(actual).hasSize(6);
    }

    @Transactional
    private void createTestStorages() {
        Storage residence = new Storage("Kate and Alex house", StorageType.RESIDENCE, null);
        entityManager.persist(residence);
        Storage room1 = new Storage("bedroom", StorageType.ROOM, residence);
        entityManager.persist(room1);
        Storage room2 = new Storage("hall", StorageType.ROOM, residence);
        entityManager.persist(room2);
        Storage furniture1 = new Storage("bed", StorageType.FURNITURE, room1);
        entityManager.persist(furniture1);
        Storage furniture2 = new Storage("wardrobe", StorageType.FURNITURE, room1);
        entityManager.persist(furniture2);
        Storage furniture3 = new Storage("wardrobe", StorageType.FURNITURE, room2);
        entityManager.persist(furniture3);
    }

}
