package org.bogacheva.training.domain.item;

import org.bogacheva.training.domain.storages.Storage;

public class Item {
    private final String name;
    private Storage storage;

    public Item(String name, Storage storage) {
        this.name = name;
        this.storage = storage;
    }

    public String getName() {
        return name;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }
}
