package org.bogacheva.training.repository.storage;

import org.bogacheva.training.domain.storage.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StorageRepository extends JpaRepository<Storage, Long> {

    // Finds all storages that are direct children of a given parent storage
    @Query("SELECT s FROM Storage s WHERE s.parent.id = :parentId")
    List<Storage> findByParentId(@Param("parentId") Long parentId);

    // Partial, case-insensitive name search for Storage
    @Query("SELECT s FROM Storage s WHERE LOWER(s.name) LIKE :pattern")
    List<Storage> findByNameLikeIgnoreCase(@Param("pattern") String pattern);
}
