package org.bogacheva.training.service.mapper;

import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface StorageMapper {

    @Mapping(target = "items", source = "items")
    @Mapping(target = "storages", expression = "java(mapStorageIds(storage.getSubStorages()))")
    @Mapping(target = "parentId", expression = "java(mapParentId(storage))")
    StorageDTO toDTO(Storage storage);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", expression = "java(new ArrayList<>())")
    @Mapping(target = "subStorages", expression = "java(new ArrayList<>())")
    @Mapping(target = "parent", ignore = true)
    Storage toEntity(StorageCreateDTO storageDTO);

    List<StorageDTO> toDTOList(List<Storage> storages);

    default List<Long> mapStorageIds(List<Storage> storages) {
        return storages.stream().map(Storage::getId).collect(Collectors.toList());
    }

    default Long mapParentId(Storage storage) {
        return storage.getParent() != null ? storage.getParent().getId() : null;
    }

}
