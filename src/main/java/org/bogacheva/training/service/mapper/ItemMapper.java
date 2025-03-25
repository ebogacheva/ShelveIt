package org.bogacheva.training.service.mapper;

import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = StorageMapper.class)
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storage", ignore = true)
    Item toEntity(ItemCreateDTO dto);

    @Mapping(source = "storage", target = "storage")
    ItemDTO toDTO(Item item);

    List<ItemDTO> toDTOList(List<Item> items);
}
