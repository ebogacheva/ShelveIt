package org.bogacheva.training.repository.item;

import org.bogacheva.training.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.storage.id = :storageId")
    List<Item> findItemsByStorageId(@Param("storageId") Long storageId);

    @Query("SELECT DISTINCT i FROM Item i JOIN i.keywords k WHERE k IN :keywords")
    List<Item> findByAnyKeyword(@Param("keywords") List<String> keywords);
}
