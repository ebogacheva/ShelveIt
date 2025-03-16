package org.bogacheva.training.domain.storages;


import org.bogacheva.training.domain.item.Item;

import java.util.ArrayList;
import java.util.List;

public class Storage implements ItemContainer {

    private String name;
    private final StorageType type;
    private final List<Item> items;
    private final List<Storage> subStorages;
    private Storage parent;

    private Storage(String name, StorageType type, Storage parent) {
        this.name = name;
        this.type = type;
        this.items = new ArrayList<>();
        this.subStorages = new ArrayList<>();
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StorageType getType() {
        return type;
    }

    public Storage getParent() {
        return parent;
    }

    public void setParent(Storage parent) {
        this.parent = parent;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public List<Item> getAll() {
        List<Item> allItems = new ArrayList<>(items);
        subStorages.forEach(subStorage -> allItems.addAll(subStorage.getAll()));
        return allItems;
    }

    public void addSubStorage(Storage storage) {
        if (!this.type.getStrategy().canContain(storage.getType())) {
            throw new IllegalArgumentException(this.type + " cannot contain " + storage.getType());
        }
        storage.setParent(this);
        subStorages.add(storage);
    }

    public List<Storage> getSubStorages() {
        return new ArrayList<>(subStorages);
    }

    public static class Builder {
        private final String name;
        private final StorageType type;
        private final Storage parent;  // NEW: Parent field

        public Builder(String name, StorageType type) {
            if (type == StorageType.RESIDENCE) {
                this.parent = null;
            } else {
                throw new IllegalArgumentException("Non-residence storages require a parent. Use Builder(name, type, parent).");
            }
            this.name = name;
            this.type = type;
        }

        public Builder(String name, StorageType type, Storage parent) {
            if (type == StorageType.RESIDENCE) {
                throw new IllegalArgumentException("Residence cannot have a parent.");
            }
            if (parent == null) {
                throw new IllegalArgumentException(type + " storage requires a parent.");
            }
            this.name = name;
            this.type = type;
            this.parent = parent;
        }

        public Storage build() {
            return new Storage(name, type, parent);
        }
    }

}
