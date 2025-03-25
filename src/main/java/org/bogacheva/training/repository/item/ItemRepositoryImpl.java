package org.bogacheva.training.repository.item;

import org.bogacheva.training.domain.item.Item;

import java.util.*;

public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public void add(Item item) {
        this.items.put(item.getId(), item);
    }

    @Override
    public void remove(Long itemId) {
        this.items.remove(itemId);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<Item> getById(Long itemId) {
        return Optional.of(items.get(itemId));
    }
}
