package com.oms.inventory.service;

import com.oms.inventory.entity.InventoryItemEntity;
import com.oms.inventory.repository.InventoryRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;

import java.util.List;

@Service
public class InventoryService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private InventoryRepository repository;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    public InventoryItemEntity addNewItem(InventoryItemEntity inventoryItemEntity) throws RuntimeException {
        try {
            InventoryItemEntity savedItem = repository.save(inventoryItemEntity);
            logger.info(String.format("Successfully saved item with id %s", inventoryItemEntity.getId()));
            return savedItem;
        } catch (Exception e) {
            logger.error(String.format("Could not add new item with id %s to the inventory db", inventoryItemEntity.getId()));
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<InventoryItemEntity> getAllItems() throws RuntimeException {
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.error("Failed to fetch all items from inventory db");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public InventoryItemEntity findItemById(int id) {
        try {
            InventoryItemEntity foundItem = repository.findById(id).orElse(null);
            if (foundItem == null) {
                logger.info(String.format("No item with id %s found in inventory", id));
            } else {
                logger.info(String.format("Item with id %s found in inventory", id));
            }
            return foundItem;
        } catch (Exception e) {
            logger.error(String.format("Failed to fetch item with id %s from the inventory", id));
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public Boolean deleteItemById(int id) {
        try {
            if (findItemById(id) == null) {
                logger.info(String.format("No item with id %s found in the invenvtory", id));
                return false;
            }
            repository.deleteById(id);
            logger.info(String.format("Deleted item with id %s in inventory", id));
            return true;
        } catch (Exception e) {
            logger.error(String.format("Could not delete id %s from inventory", id));
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public InventoryItemEntity updateItem(InventoryItemEntity item) throws RuntimeException {
        try {
            if (findItemById(item.getId()) == null ) {
                logger.info(String.format("No item with id %s found in the invenvtory", item.getId()));
                return null;
            }
            InventoryItemEntity updatedItem = repository.save(item);
            logger.info(String.format("Updated item with id %s in inventory", item.getId()));
            return updatedItem;
        } catch (Exception e) {
            logger.error(String.format("Failed to update item with id %s in inventory", item.getId()));
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
