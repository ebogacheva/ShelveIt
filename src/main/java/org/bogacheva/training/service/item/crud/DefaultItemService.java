package org.bogacheva.training.service.item.crud;

import org.bogacheva.training.ai.EmbeddingService;
import org.bogacheva.training.exceptions.ItemNotFoundException;
import org.bogacheva.training.exceptions.StorageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.bogacheva.training.domain.item.Item;
import org.bogacheva.training.domain.storage.Storage;
import org.bogacheva.training.repository.item.ItemRepository;
import org.bogacheva.training.repository.storage.StorageRepository;
import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.ItemUpdateDTO;
import org.bogacheva.training.service.mapper.ItemMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultItemService implements ItemService {

    private final ItemRepository itemRepo;
    private final StorageRepository storageRepo;
    private final ItemMapper itemMapper;
    private final EmbeddingService embeddingService;

    public DefaultItemService(ItemRepository itemRepo,
                              StorageRepository storageRepo,
                              ItemMapper itemMapper,
                              @Autowired(required = false) EmbeddingService embeddingService) {
        this.itemRepo = itemRepo;
        this.storageRepo = storageRepo;
        this.itemMapper = itemMapper;
        this.embeddingService = embeddingService;
    }

    @Override
    @Transactional
    public ItemDTO create(ItemCreateDTO dto) {
        Storage storage = getStorageByIdOrThrow(dto.getStorageId());
        Item newItem = itemMapper.toEntity(dto);
        normalizeKeywords(newItem);
        newItem.setStorage(storage);
        Item savedItem = itemRepo.save(newItem);
        computeAndSaveEmbedding(savedItem);
        return itemMapper.toDTO(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDTO getById(Long itemId) {
        Item item = getItemOrThrow(itemId);
        return itemMapper.toDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getAll() {
        return itemMapper.toDTOList(itemRepo.findAll());
    }

    @Override
    @Transactional
    public void delete(Long itemId) {
        getItemOrThrow(itemId);
        itemRepo.deleteById(itemId);
    }

    @Override
    @Transactional
    public ItemDTO update(Long itemId, ItemUpdateDTO dto) {
        validateUpdateDTO(dto);
        Item item = getItemOrThrow(itemId);
        applyChanges(item, dto);
        normalizeKeywords(item);
        Item savedItem = itemRepo.save(item);
        computeAndSaveEmbedding(savedItem);
        return itemMapper.toDTO(savedItem);
    }

    private Item getItemOrThrow(Long itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID must not be null.");
        }
        return itemRepo.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    private Storage getStorageByIdOrThrow(Long storageId) {
        if (storageId == null) {
            throw new IllegalArgumentException("Storage ID must not be null.");
        }
        return storageRepo.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));
    }

    private void validateUpdateDTO(ItemUpdateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("ItemUpdateDTO must not be null.");
        }
        boolean hasName = dto.getName() != null;
        boolean hasKeywords = dto.getKeywords() != null;
        boolean hasStorageId = dto.getStorageId() != null;
        boolean hasAttributes = dto.getAttributes() != null;

        if (!hasName && !hasKeywords && !hasStorageId && !hasAttributes) {
            throw new IllegalArgumentException("At least one field must be provided for update.");
        }
        if (dto.getName() != null && dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name, if provided, cannot be empty.");
        }
    }

    private void applyChanges(Item item, ItemUpdateDTO dto) {
        Optional.ofNullable(dto.getName()).ifPresent(item::setName);
        Optional.ofNullable(dto.getKeywords())
                .ifPresent(kws -> item.setKeywords(new ArrayList<>(kws)));
        Optional.ofNullable(dto.getAttributes()).ifPresent(item::setAttributes);
        if (dto.getStorageId() != null && !dto.getStorageId().equals(item.getStorage().getId())) {
            item.setStorage(getStorageByIdOrThrow(dto.getStorageId()));
        }
    }

    private void normalizeKeywords(Item item) {
        List<String> keywords = item.getKeywords();
        if (keywords != null) {
            List<String> normalized = keywords.stream()
                    .filter(Objects::nonNull)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            item.setKeywords(normalized);
        }
    }

    private void computeAndSaveEmbedding(Item item) {
        if (embeddingService == null) return;
        float[] vector = embeddingService.embed(buildEmbeddingText(item));
        if (vector == null || vector.length == 0) return;
        itemRepo.updateEmbedding(item.getId(), embeddingService.toVectorString(vector));
    }

    private String buildEmbeddingText(Item item) {
        StringBuilder sb = new StringBuilder(item.getName());
        boolean hasAttributes = item.getAttributes() != null && !item.getAttributes().isEmpty();
        if (hasAttributes) {
            item.getAttributes().forEach((k, v) -> sb.append(" ").append(k).append(":").append(v));
        } else if (item.getKeywords() != null && !item.getKeywords().isEmpty()) {
            sb.append(" ").append(String.join(" ", item.getKeywords()));
        }
        return sb.toString();
    }
}
