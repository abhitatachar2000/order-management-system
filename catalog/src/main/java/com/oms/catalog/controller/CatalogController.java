package com.oms.catalog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.catalog.dto.CatalogDTO;
import com.oms.catalog.entity.CatalogItemEntity;
import com.oms.catalog.repository.CatalogRepository;
import com.oms.catalog.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/catalog")
public class CatalogController {

    @Autowired
    private ObjectMapper objectMapper;

    public final CatalogService catalogService;

    @Autowired
    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/items")
    public ResponseEntity<List<CatalogItemEntity>> getAllItems() throws JsonProcessingException {
        try {
            List<CatalogItemEntity> items = catalogService.getAllCatalogItems();
            return ResponseEntity.status(200).body(items);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<CatalogItemEntity>());
        }
    }

    @PostMapping("/items")
    public ResponseEntity<CatalogItemEntity> createNewItem(@RequestBody CatalogDTO catalogDTO) {
        try {
            CatalogItemEntity itemEntity = convertDTOToEntity(catalogDTO);
            catalogService.addNewItem(itemEntity);
            return ResponseEntity.status(201).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new CatalogItemEntity());
        }
    }

    @RequestMapping("/items/{id}")
    public ResponseEntity<CatalogItemEntity> returnSingleItem(@PathVariable int id) {
        try {
            CatalogItemEntity catalogItem = catalogService.getItemById(id);
            if (catalogItem == null) {
                throw new RuntimeException(String.format("No item found with id %s", id));
            }
            return ResponseEntity.status(200).body(catalogItem);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/items/search")
    public ResponseEntity<List<CatalogItemEntity>> findItemsOfCategory(@RequestParam String category){
        try {
            List<CatalogItemEntity> catalogItems = catalogService.getItemsByCategory(category);
            return ResponseEntity.status(200).body(catalogItems);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<CatalogItemEntity> updateCatalogItem(@PathVariable int id, @RequestBody CatalogDTO catalogDTO) {
        try {
            CatalogItemEntity catalogItemEntity = convertDTOToEntity(catalogDTO);
            CatalogItemEntity catalogItem = catalogService.updateItem(id, catalogItemEntity);
            return ResponseEntity.status(200).body(catalogItem);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<CatalogItemEntity> deleteCatalogItem(@PathVariable int id) {
        try {
            Boolean itemDeleted = catalogService.deleteById(id);
            if (!itemDeleted) {
                throw new RuntimeException(String.format("Could not delete item with id: %s", id));
            }
            return ResponseEntity.status(204).build();
        } catch (Exception e){
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/items")
    public ResponseEntity<CatalogItemEntity> deleteByCategory(@RequestParam String category) {
        try {
            Boolean itemsDeleted = catalogService.deleteAllItemsOfCategory(category);
            if (!itemsDeleted) {
                throw new RuntimeException(String.format("Could not delete items of category: %s", category));
            }
            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    private CatalogItemEntity convertDTOToEntity(CatalogDTO catalogDTO) {
        return new CatalogItemEntity(
                catalogDTO.getName(),
                catalogDTO.getPricePerUnit(),
                catalogDTO.getCategory(),
                catalogDTO.getAvailableStock()
        );
    }

}
