package org.bogacheva.training.service.mapper;

import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SequencedCollection;
import java.util.stream.Collectors;

@Component
public class StorageMapperHelper {

    @Named("mapItemIds")
    public List<Long> mapItemIds(List<Item> items) {
        return items == null ? Collections.emptyList() :
                items.stream().map(Item::getId).collect(Collectors.toList());
    }

    @Named("mapStorageIds")
    public List<Long> mapStorageIds(List<Storage> storages) {
        return storages == null ? Collections.emptyList() :
                storages.stream().map(Storage::getId).collect(Collectors.toList());
    }

    @Named("mapParentId")
    public Long mapParentId(Storage parent) {
        return parent != null ? parent.getId() : null;
    }
}
