package org.bogacheva.training.client;

import org.bogacheva.training.service.dto.ItemCreateDTO;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.ItemUpdateDTO;
import org.bogacheva.training.service.dto.StorageCreateDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.dto.StorageUpdateDTO;
import org.bogacheva.training.domain.storage.StorageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class RestClient {
    
    private static final String ITEMS_PATH = "/items";
    private static final String STORAGES_PATH = "/storages";
    private static final String SEARCH_PATH = "/search";
    private static final String NEAR_PATH = "/near";
    private static final String SUBSTORAGES_PATH = "/substorages";
    private static final String TRACK_STORAGES_PATH = "/trackStorages";
    
    private final RestTemplate restTemplate;
    private final String backendApiUrl;
    
    public RestClient(RestTemplate restTemplate, 
                     @Value("${backend.api.url:http://localhost:8080/api}") String backendApiUrl) {
        this.restTemplate = restTemplate;
        this.backendApiUrl = backendApiUrl;
    }
    
    public ItemDTO createItem(ItemCreateDTO createDTO) {
        ResponseEntity<ItemDTO> response = restTemplate.postForEntity(
            backendApiUrl + ITEMS_PATH, 
            createDTO, 
            ItemDTO.class
        );
        return response.getBody();
    }
    
    public StorageDTO createStorage(StorageCreateDTO createDTO) {
        ResponseEntity<StorageDTO> response = restTemplate.postForEntity(
            backendApiUrl + STORAGES_PATH, 
            createDTO, 
            StorageDTO.class
        );
        return response.getBody();
    }
    
    public List<ItemDTO> getAllItems() {
        ResponseEntity<List<ItemDTO>> response = restTemplate.exchange(
            backendApiUrl + ITEMS_PATH,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ItemDTO>>() {}
        );
        return response.getBody();
    }
    
    public List<StorageDTO> getAllStorages(StorageType type) {
        String url = backendApiUrl + STORAGES_PATH;
        if (type != null) {
            url += "?type=" + type;
        }
        ResponseEntity<List<StorageDTO>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<StorageDTO>>() {}
        );
        return response.getBody();
    }
    
    public void deleteItem(Long id) {
        restTemplate.delete(backendApiUrl + ITEMS_PATH + "/" + id);
    }
    
    public void deleteStorage(Long id) {
        restTemplate.delete(backendApiUrl + STORAGES_PATH + "/" + id);
    }
    
    public ItemDTO getItemById(Long id) {
        return restTemplate.getForObject(backendApiUrl + ITEMS_PATH + "/" + id, ItemDTO.class);
    }
    
    public StorageDTO getStorageById(Long id) {
        return restTemplate.getForObject(backendApiUrl + STORAGES_PATH + "/" + id, StorageDTO.class);
    }
    
    public List<ItemDTO> getItemsByStorageId(Long storageId) {
        ResponseEntity<List<ItemDTO>> response = restTemplate.exchange(
            backendApiUrl + ITEMS_PATH + "/" + storageId,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ItemDTO>>() {}
        );
        return response.getBody();
    }
    
    public List<StorageDTO> getSubStorages(Long storageId) {
        ResponseEntity<List<StorageDTO>> response = restTemplate.exchange(
            backendApiUrl + STORAGES_PATH + "/" + storageId + SUBSTORAGES_PATH,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<StorageDTO>>() {}
        );
        return response.getBody();
    }
    
    public List<ItemDTO> searchItems(String name, List<String> keywords) {
        StringBuilder url = new StringBuilder(backendApiUrl + ITEMS_PATH + SEARCH_PATH + "?");
        if (name != null && !name.isEmpty()) {
            url.append("name=").append(name).append("&");
        }
        if (keywords != null && !keywords.isEmpty()) {
            for (String keyword : keywords) {
                url.append("keywords=").append(keyword).append("&");
            }
        }
        
        ResponseEntity<List<ItemDTO>> response = restTemplate.exchange(
            url.toString(),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ItemDTO>>() {}
        );
        return response.getBody();
    }
    
    public List<StorageDTO> searchStorages(String name, StorageType type) {
        StringBuilder url = new StringBuilder(backendApiUrl + STORAGES_PATH + SEARCH_PATH + "?");
        if (name != null && !name.isEmpty()) {
            url.append("name=").append(name).append("&");
        }
        if (type != null) {
            url.append("type=").append(type).append("&");
        }
        
        ResponseEntity<List<StorageDTO>> response = restTemplate.exchange(
            url.toString(),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<StorageDTO>>() {}
        );
        return response.getBody();
    }
    
    public List<ItemDTO> getItemsNear(Long itemId) {
        ResponseEntity<List<ItemDTO>> response = restTemplate.exchange(
            backendApiUrl + ITEMS_PATH + "/" + itemId + NEAR_PATH,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ItemDTO>>() {}
        );
        return response.getBody();
    }
    
    public List<Long> getStorageHierarchyIds(Long itemId) {
        ResponseEntity<List<Long>> response = restTemplate.exchange(
            backendApiUrl + ITEMS_PATH + "/" + itemId + TRACK_STORAGES_PATH,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Long>>() {}
        );
        return response.getBody();
    }
    
    public ItemDTO updateItem(Long itemId, ItemUpdateDTO updateDTO) {
        restTemplate.put(backendApiUrl + ITEMS_PATH + "/" + itemId, updateDTO);
        return getItemById(itemId);
    }
    
    public StorageDTO updateStorage(Long storageId, StorageUpdateDTO updateDTO) {
        ResponseEntity<StorageDTO> response = restTemplate.exchange(
            backendApiUrl + STORAGES_PATH + "/" + storageId,
            HttpMethod.PATCH,
            new org.springframework.http.HttpEntity<>(updateDTO),
            StorageDTO.class
        );
        return response.getBody();
    }
}

