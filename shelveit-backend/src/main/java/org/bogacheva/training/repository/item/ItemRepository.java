package org.bogacheva.training.repository.item;

import jakarta.validation.constraints.NotNull;
import org.bogacheva.training.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for {@link Item} entities.
 * Provides custom queries for item search and retrieval operations.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Finds all items stored in a specific storage.
     *
     * @param storageId the ID of the storage
     * @return list of items in the given storage
     */
    @Query("SELECT i FROM Item i WHERE i.storage.id = :storageId")
    List<Item> findItemsByStorageId(@Param("storageId") Long storageId);

    /**
     * Finds items that have at least one of the specified keywords (case-insensitive).
     *
     * @param keywords list of keywords to search for
     * @return list of items matching any of the keywords
     */
    @Query("SELECT DISTINCT i FROM Item i JOIN i.keywords k WHERE LOWER(k) IN :keywords")
    List<Item> findByAnyKeyword(@Param("keywords") List<String> keywords);

    /**
     * Finds all items in the same storage where the specified item is located,
     * excluding the item itself.
     *
     * @param storageId the ID of the storage
     * @param excludeItemId the ID of the item to exclude from results
     * @return list of items in the storage excluding the specified item
     */
    @Query("SELECT i FROM Item i WHERE i.storage.id = :storageId AND i.id <> :excludeItemId")
    List<Item> findItemsByStorageIdAndExcludeItemId(@Param("storageId") Long storageId, @Param("excludeItemId") @NotNull Long excludeItemId);

    /**
     * Retrieves the hierarchy of storage IDs for the storage containing the specified item,
     * including all parent storages up the hierarchy.
     *
     * @param itemId the ID of the item
     * @return list of storage IDs in the hierarchy
     */
    @Query(value = """ 
    WITH RECURSIVE storage_hierarchy AS (
        SELECT s.id, s.parent_id
        FROM storages s
        JOIN items i ON i.storage_id = s.id
        WHERE i.id = :itemId
        UNION ALL
        SELECT parent.id, parent.parent_id
        FROM storages parent
        JOIN storage_hierarchy sh ON sh.parent_id = parent.id
    )
    SELECT id FROM storage_hierarchy
    """, nativeQuery = true)
    List<Long> findStorageHierarchyIds(@Param("itemId") Long itemId);

    /**
     * Finds all items whose names match the given pattern, ignoring case.
     *
     * @param pattern the search pattern for item names, case-insensitive, using SQL LIKE syntax
     * @return a list of Item entities whose names match the pattern ignoring case
     */
    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE :pattern")
    List<Item> findByNameLikeIgnoreCase(@Param("pattern") String pattern);
}
