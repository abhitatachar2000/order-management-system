package com.oms.catalog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.catalog.dto.CatalogDTO;
import com.oms.catalog.entity.CatalogItemEntity;
import com.oms.catalog.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/items")
    public ResponseEntity<List<CatalogItemEntity>> getAllItems() throws JsonProcessingException {
        logger.info("Received request to fetch all items in the catalog");
        try {
            List<CatalogItemEntity> items = catalogService.getAllCatalogItems();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @PostMapping("/items")
    public ResponseEntity<Void> createNewItem(@RequestBody CatalogDTO catalogDTO) {
        logger.info("Received request to create a new item in the catalog");
        try {
            CatalogItemEntity itemEntity = convertDTOToEntity(catalogDTO);
            catalogService.addNewItem(itemEntity);
            return ResponseEntity.status(201).build(); // 201 Created
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<CatalogItemEntity> returnSingleItem(@PathVariable int id) {
        logger.info(String.format("Received request to fetch item with id %s in the catalog", id));
        try {
            CatalogItemEntity catalogItem = catalogService.getItemById(id);
            if (catalogItem == null) {
                return ResponseEntity.status(404).build(); // 404 Not Found
            }
            return ResponseEntity.ok(catalogItem); // 200 OK
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/items/search")
    public ResponseEntity<List<CatalogItemEntity>> findItemsOfCategory(@RequestParam String category) {
        logger.info(String.format("Received request to fetch all items of category %s in the catalog", category));
        try {
            List<CatalogItemEntity> catalogItems = catalogService.getItemsByCategory(category);
            return ResponseEntity.ok(catalogItems);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<CatalogItemEntity> updateCatalogItem(@PathVariable int id, @RequestBody CatalogDTO catalogDTO) {
        logger.info(String.format("Received request to update item with id %s in the catalog", id));
        try {
            CatalogItemEntity catalogItemEntity = convertDTOToEntity(catalogDTO);
            CatalogItemEntity catalogItem = catalogService.updateItem(id, catalogItemEntity);
            return ResponseEntity.ok(catalogItem);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteCatalogItem(@PathVariable int id) {
        logger.info(String.format("Received request to delete item with id %s in the catalog", id));
        try {
            Boolean itemDeleted = catalogService.deleteById(id);
            if (!itemDeleted) {
                return ResponseEntity.status(404).build(); // 404 Not Found
            }
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> deleteByCategory(@RequestParam String category) {
        logger.info(String.format("Received request to delete all items of category %s in the catalog", category));
        try {
            Boolean itemsDeleted = catalogService.deleteAllItemsOfCategory(category);
            if (!itemsDeleted) {
                return ResponseEntity.status(404).build(); // 404 Not Found
            }
            return ResponseEntity.noContent().build(); // 204 No Content
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
