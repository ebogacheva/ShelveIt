package org.bogacheva.training.domain.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bogacheva.training.domain.storage.Storage;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private Long id;
    private String name;
    private Storage storage;

    public Item(String name, Storage storage) {
        this.name = name;
        this.storage = storage;
    }
}
