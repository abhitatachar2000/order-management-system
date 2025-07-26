package com.oms.inventory.controller;

import com.oms.inventory.dto.InventoryItemDTO;
import com.oms.inventory.entity.InventoryItemEntity;
import com.oms.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/items")
    public ResponseEntity<?> getAllItems() {
        try {
            List<InventoryItemEntity> items = inventoryService.getAllItems();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/items")
    public ResponseEntity<?> addNewItem(@RequestBody InventoryItemDTO inventoryItemDTO) {
        try {
            if (inventoryService.findItemById(inventoryItemDTO.getId()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(String.format("Item with id %s already exists, try updating instead.", inventoryItemDTO.getId()));
            }
            InventoryItemEntity entity = dtoToEntityConverter(inventoryItemDTO);
            inventoryService.addNewItem(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(entity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<?> findSingleItem(@PathVariable("id") int id){
        try {
            InventoryItemEntity item = inventoryService.findItemById(id);
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Item with id %s not found", id));
            } else {
                return ResponseEntity.ok(item);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/items/item")
    public ResponseEntity<?> deleteSingleItem(@RequestParam("id") int id) {
        try {
            Boolean itemDeleted = inventoryService.deleteItemById(id);
            if (!itemDeleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("No item found with id %s", id));
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/items")
    public ResponseEntity<?> updateItemById( @RequestBody InventoryItemDTO item){
        try {
            InventoryItemEntity entity = dtoToEntityConverter(item);
            InventoryItemEntity updatedEntity = inventoryService.updateItem(entity);
            if (updatedEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("No item found with id %s. Try creating a new item", entity.getId()));
            } else {
                return ResponseEntity.status(HttpStatus.OK).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private InventoryItemEntity dtoToEntityConverter(InventoryItemDTO inventoryItemDTO) {
        return new InventoryItemEntity(
                inventoryItemDTO.getId(),
                inventoryItemDTO.getQuantity()
        );
    }
}
