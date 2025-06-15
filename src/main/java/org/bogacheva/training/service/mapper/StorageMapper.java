package org.bogacheva.training.service.mapper;

import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, StorageMapperHelper.class})
public interface StorageMapper {

    @Mapping(target = "items", source = "items", qualifiedByName = "mapItemIds")
    @Mapping(target = "storages", source = "subStorages", qualifiedByName = "mapStorageIds")
    @Mapping(target = "parentId", source = "parent", qualifiedByName = "mapParentId")
    StorageDTO toDTO(Storage storage);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "subStorages", ignore = true)
    @Mapping(target = "parent", ignore = true)
    Storage toEntity(StorageCreateDTO storageDTO);

    List<StorageDTO> toDTOList(List<Storage> storages);
}
