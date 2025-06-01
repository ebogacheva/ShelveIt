package org.bogacheva.training.repository.item;

import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.storage.id = :storageId")
    List<Item> findItemsByStorageId(@Param("storageId") Long storageId);

    @Query("SELECT DISTINCT i FROM Item i JOIN i.keywords k WHERE k IN :keywords")
    List<Item> findByAnyKeyword(@Param("keywords") List<String> keywords);

    @Query("SELECT i FROM Item i WHERE i.storage.id = :storageId AND i.id <> :excludeItemId")
    List<Item> findItemsByStorageIdAndExcludeItemId(@Param("storageId") Long storageId, @Param("excludeItemId") Long excludeItemId);

   @Query(value = """ 
        WITH RECURSIVE storage_hierarchy AS (
            SELECT s.id, s.parent_id
            FROM storage s
            JOIN item i ON i.storage_id = s.id
            WHERE i.id = :itemId
            UNION ALL
            SELECT parent.id, parent.parent_id
            FROM storage parent
            JOIN storage_hierarchy sh ON sh.parent_id = parent.id
        )
        SELECT id FROM storage_hierarchy
        """, nativeQuery = true)
    List<Long> findStorageHierarchyIds(@Param("itemId") Long itemId); // TODO: add stored procedure for this query



}
