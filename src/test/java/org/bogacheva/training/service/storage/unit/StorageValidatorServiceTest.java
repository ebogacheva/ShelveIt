package org.bogacheva.training.service.storage.unit;

import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.exceptions.InvalidStorageHierarchyException;
import org.bogacheva.training.service.storage.StorageValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class StorageValidatorServiceTest {

    private StorageValidatorService validator;

    @BeforeEach
    void setUp() {
        validator = new StorageValidatorService();
    }

    @Nested
    @DisplayName("validateStorageCreation tests")
    class ValidateStorageCreationTests {

        @Test
        @DisplayName("Should pass for RESIDENCE without parent")
        void validateStorageCreation_shouldPassForResidenceWithoutParent() {
            // Arrange
            StorageCreateDTO dto = new StorageCreateDTO("Home", StorageType.RESIDENCE, null);
            // Act & Assert - no exception should be thrown
            assertDoesNotThrow(() -> validator.validateStorageCreation(dto));
        }

        @Test
        @DisplayName("Should throw exception for RESIDENCE with parent")
        void validateStorageCreation_shouldThrowForResidenceWithParent() {
            // Arrange
            StorageCreateDTO dto = new StorageCreateDTO("Home", StorageType.RESIDENCE, 1L);

            // Act & Assert - should throw InvalidStorageHierarchyException
            InvalidStorageHierarchyException exception = assertThrows(
                    InvalidStorageHierarchyException.class,
                    () -> validator.validateStorageCreation(dto)
            );
            assertTrue(exception.getMessage().contains("RESIDENCE storage should not have a parent"));
        }

        @ParameterizedTest
        @EnumSource(value = StorageType.class, names = {"ROOM", "FURNITURE", "UNIT"})
        @DisplayName("Should throw exception for non-RESIDENCE without parent")
        void validateStorageCreation_shouldThrowForNonResidenceWithoutParent(StorageType type) {
            // Arrange
            StorageCreateDTO dto = new StorageCreateDTO("Storage", type, null);

            // Act & Assert
            InvalidStorageHierarchyException exception = assertThrows(
                    InvalidStorageHierarchyException.class,
                    () -> validator.validateStorageCreation(dto)
            );
            assertTrue(exception.getMessage().contains("requires a parent"));
        }

        @ParameterizedTest
        @EnumSource(value = StorageType.class, names = {"ROOM", "FURNITURE", "UNIT"})
        @DisplayName("Should pass for non-RESIDENCE with parent")
        void validateStorageCreation_shouldPassForNonResidenceWithParent(StorageType type) {
            // Arrange
            StorageCreateDTO dto = new StorageCreateDTO("Storage", type, 1L);

            // Act & Assert - no exception should be thrown
            assertDoesNotThrow(() -> validator.validateStorageCreation(dto));
        }

        @Test
        @DisplayName("Should throw exception for null DTO")
        void validateStorageCreation_shouldThrowForNullDto() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> validator.validateStorageCreation(null)
            );
            assertEquals("Storage creation DTO cannot be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("validateParentExistenceRules tests")
    class ValidateParentExistenceRulesTests {

        @Test
        @DisplayName("Should pass for RESIDENCE without parent")
        void shouldPassForResidenceWithoutParent() {
            // Act & Assert
            assertDoesNotThrow(() ->
                    validator.validateParentExistenceRules(null, StorageType.RESIDENCE)
            );
        }

        @Test
        @DisplayName("Should throw for RESIDENCE with parent")
        void shouldThrowForResidenceWithParent() {
            // Arrange
            Storage parent = createStorage(1L, "Parent", StorageType.RESIDENCE, null);

            // Act & Assert
            InvalidStorageHierarchyException exception = assertThrows(
                    InvalidStorageHierarchyException.class,
                    () -> validator.validateParentExistenceRules(parent, StorageType.RESIDENCE)
            );
            assertTrue(exception.getMessage().contains("RESIDENCE cannot have a parent"));
        }

        @ParameterizedTest
        @EnumSource(value = StorageType.class, names = {"ROOM", "FURNITURE", "UNIT"})
        @DisplayName("Should pass for non-RESIDENCE with parent")
        void shouldPassForNonResidenceWithParent(StorageType type) {
            // Arrange
            Storage parent = createStorage(1L, "Parent", StorageType.RESIDENCE, null);

            // Act & Assert
            assertDoesNotThrow(() -> validator.validateParentExistenceRules(parent, type));
        }

        @ParameterizedTest
        @EnumSource(value = StorageType.class, names = {"ROOM", "FURNITURE", "UNIT"})
        @DisplayName("Should throw for non-RESIDENCE without parent")
        void shouldThrowForNonResidenceWithoutParent(StorageType type) {
            // Act & Assert
            InvalidStorageHierarchyException exception = assertThrows(
                    InvalidStorageHierarchyException.class,
                    () -> validator.validateParentExistenceRules(null, type)
            );
            assertTrue(exception.getMessage().contains("requires a parent"));
        }

        @Test
        @DisplayName("Should throw for null storage type")
        void shouldThrowForNullStorageType() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> validator.validateParentExistenceRules(null, null)
            );
            assertEquals("Storage type cannot be null for checking hierarchy rules.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("validateHierarchySubStorageRules tests")
    class ValidateHierarchySubStorageRulesTests {

        @Test
        @DisplayName("Should pass if parent type allows containing the sub-storage type")
        void shouldPassForValidParentChildTypes() {
            // Arrange
            StorageType parentType = StorageType.ROOM;
            StorageType subStorageType = StorageType.FURNITURE;

            // Act & Assert
            assertDoesNotThrow(() ->
                    validator.validateHierarchySubStorageRules(parentType, subStorageType)
            );
        }

        @Test
        @DisplayName("Should throw exception if parent type does not allow containing sub-storage type")
        void shouldThrowForInvalidParentChildTypes() {
            // Arrange
            StorageType parentType = StorageType.UNIT;
            StorageType subStorageType = StorageType.RESIDENCE;

            // Act & Assert
            InvalidStorageHierarchyException exception = assertThrows(
                    InvalidStorageHierarchyException.class,
                    () -> validator.validateHierarchySubStorageRules(parentType, subStorageType)
            );
            assertTrue(exception.getMessage().contains("cannot contain"));
        }

        @Test
        @DisplayName("Should throw for null parent type")
        void shouldThrowForNullParentType() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> validator.validateHierarchySubStorageRules(null, StorageType.ROOM)
            );
            assertEquals("Parent type cannot be null for checking parent-storage compatibility.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw for null sub-storage type")
        void shouldThrowForNullSubStorageType() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> validator.validateHierarchySubStorageRules(StorageType.ROOM, null)
            );
            assertEquals("SubStorage type cannot be null for checking parent-storage compatibility.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("validateTypeUpdate tests")
    class ValidateTypeUpdateTests {

        @Test
        @DisplayName("Should pass if type update respects hierarchy rules")
        void shouldPassForValidTypeUpdate() {
            // Arrange
            Storage parent = createStorage(2L, "Parent", StorageType.RESIDENCE, null);
            Storage storage = createStorage(1L, "Storage", StorageType.ROOM, parent);
            StorageType newType = StorageType.FURNITURE;

            // Act & Assert
            assertDoesNotThrow(() -> validator.validateTypeUpdate(storage, newType));
        }

        @Test
        @DisplayName("Should throw exception if parent cannot accommodate the type change")
        void shouldThrowForInvalidTypeUpdateWithParent() {
            // Arrange
            Storage parent = createStorage(2L, "Parent", StorageType.RESIDENCE, null);
            Storage storage = createStorage(1L, "Storage", StorageType.UNIT, parent);

            // Act & Assert
            assertThrows(InvalidStorageHierarchyException.class,
                    () -> validator.validateTypeUpdate(storage, StorageType.RESIDENCE)
            );
        }

        @Test
        @DisplayName("Should throw for null storage")
        void shouldThrowForNullStorage() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> validator.validateTypeUpdate(null, StorageType.ROOM)
            );
            assertEquals("Storage cannot be null for checking changes of storage type.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw for null new type")
        void shouldThrowForNullNewType() {
            // Arrange
            Storage storage = createStorage(1L, "Storage", StorageType.ROOM, null);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> validator.validateTypeUpdate(storage, null)
            );
            assertEquals("New storage type is required for checking changes of storage type.", exception.getMessage());
        }
    }

    private Storage createStorage(Long id, String name, StorageType type, Storage parent) {
        Storage storage = new Storage();
        storage.setId(id);
        storage.setName(name);
        storage.setType(type);
        storage.setParent(parent);
        storage.setItems(new ArrayList<>());
        storage.setSubStorages(new ArrayList<>());
        return storage;
    }
}
