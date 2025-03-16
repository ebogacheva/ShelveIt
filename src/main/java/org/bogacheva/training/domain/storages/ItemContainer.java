package org.bogacheva.training.domain.storages;

import org.bogacheva.training.domain.item.Item;

import java.util.List;

public interface ItemContainer {
    void addItem(Item item);
    void removeItem(Item item);
    List<Item> getAll();
}
