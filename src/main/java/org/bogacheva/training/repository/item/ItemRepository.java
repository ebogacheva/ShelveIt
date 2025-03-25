package org.bogacheva.training.repository.item;

import org.bogacheva.training.domain.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    void add(Item item);
    void remove(Long itemId);
    List<Item> getAll();
    Optional<Item> getById(Long itemId);
}
