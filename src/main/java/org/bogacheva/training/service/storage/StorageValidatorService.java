package org.bogacheva.training.service.storage;

import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.domain.storage.StorageType;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.exceptions.InvalidStorageHierarchyException;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class StorageValidatorService {

    /**
     * Validates the creation data for a storage.
     *
     * @param dto the storage creation data
     * @throws InvalidStorageHierarchyException if the creation data violates hierarchy rules
     */
    public void validateStorageCreation(StorageCreateDTO dto) {
        validateNotNull(dto, () -> new IllegalArgumentException("Storage creation DTO cannot be null"));
        if (dto.getType() == StorageType.RESIDENCE && dto.getParentId() != null) {
            throw new InvalidStorageHierarchyException("RESIDENCE storage should not have a parent.");
        }

        if (dto.getType() != StorageType.RESIDENCE && dto.getParentId() == null) {
            throw new InvalidStorageHierarchyException(
                    "Storage type " + dto.getType() + " requires a parent.");
        }
    }

    /**
     * Validates the parent's existence rules for a storage type and its parent.
     *
     * @param type   the storage type to validate
     * @param parent the parent storage, can be null
     * @throws InvalidStorageHierarchyException if the hierarchy rules are violated
     */
    public void validateParentExistenceRules(Storage parent, StorageType type) {
        validateNotNull(type, () -> new IllegalArgumentException(
                "Storage type cannot be null for checking hierarchy rules."));
        boolean hasParent = parent != null;
        if (!type.getStrategy().canHaveParent() && hasParent) {
            throw new InvalidStorageHierarchyException(type + " cannot have a parent");
        }
        if (type.getStrategy().canHaveParent() && !hasParent) {
            throw new InvalidStorageHierarchyException(type + " storage requires a parent.");
        }
    }

    /**
     * Validates if the parent storage can contain the child storage type.
     *
     * @param parentType the parent storage type
     * @param subStorageType  the child storage type
     * @throws InvalidStorageHierarchyException if the parent cannot contain the child
     */
    public void validateHierarchySubStorageRules(StorageType parentType, StorageType subStorageType) {
        validateNotNull(parentType, () -> new IllegalArgumentException(
                "Parent type cannot be null for checking parent-storage compatibility."));
        validateNotNull(subStorageType, () -> new IllegalArgumentException(
                "SubStorage type cannot be null for checking parent-storage compatibility."));
        if (!parentType.getStrategy().canContain(subStorageType)) {
            throw new InvalidStorageHierarchyException(
                    parentType + " cannot contain " + subStorageType);
        }
    }

    /**
     * Validates if the storage type can be updated to a new type.
     *
     * @param storage the storage to validate
     * @param newType the new storage type
     * @throws InvalidStorageHierarchyException if the update violates hierarchy rules
     */
    public void validateTypeUpdate(Storage storage, StorageType newType) {
        validateNotNull(storage, () -> new IllegalArgumentException(
                "Storage cannot be null for checking changes of storage type."));
        validateNotNull(newType, () -> new IllegalArgumentException(
                "New storage type is required for checking changes of storage type."));
        validateParentExistenceRules(storage.getParent(), newType);
        if (storage.getParent() != null) {
            validateHierarchySubStorageRules(storage.getParent().getType(), newType);
        }
        for (Storage subStorage : storage.getSubStorages()) {
            validateHierarchySubStorageRules(newType, subStorage.getType());
        }
    }


    /**
     * Validates that an object is not null, throwing the supplied exception if it is.
     *
     * @param obj The object to check
     * @param exceptionSupplier A supplier that creates the exception to throw
     * @throws RuntimeException The exception produced by the supplier if obj is null
     */
    public <T extends RuntimeException> void validateNotNull(
            Object obj,
            Supplier<T> exceptionSupplier) {
        if (obj == null) {
            throw exceptionSupplier.get();
        }
    }
}
