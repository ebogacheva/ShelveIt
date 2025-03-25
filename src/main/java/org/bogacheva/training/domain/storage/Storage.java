package org.bogacheva.training.domain.storage;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bogacheva.training.domain.item.Item;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Storage {

    private Long id;
    private String name;
    private StorageType type;
    private List<Item> items;
    private List<Storage> subStorages;
    private Storage parent;

    public Storage(String name, StorageType type, Storage parent) {
        this.name = name;
        this.type = type;
        this.items = new ArrayList<>();
        this.subStorages = new ArrayList<>();
        this.parent = parent;
    }
}
