package org.bogacheva.training.service.storage;

import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.mapper.StorageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class StorageServiceIntegrationTest {

    @Autowired
    private StorageService storageService;

    @Autowired
    private StorageRepository storageRepo;

    @Autowired
    private StorageMapper storageMapper;

    @BeforeEach
    void setUp() {
        storageRepo.deleteAll();
    }

    @Test
    void shouldReturnStorageDTOWithExpectedValues_WhenValidResidenceDTOIsProvided() {
        StorageCreateDTO residenceDTO = new StorageCreateDTO("Storage Name", StorageType.RESIDENCE, null);

        StorageDTO actual = storageService.create(residenceDTO);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getName()).isEqualTo("Storage Name");
        assertThat(actual.getType()).isEqualTo(StorageType.RESIDENCE);
        assertThat(actual.getParentId()).isNull();
        assertThat(actual.getStorages()).isEmpty();
        assertThat(actual.getItems()).isEmpty();
    }

}
