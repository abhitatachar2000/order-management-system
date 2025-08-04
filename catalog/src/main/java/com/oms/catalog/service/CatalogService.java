package com.oms.catalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oms.catalog.entity.CatalogItemEntity;
import com.oms.catalog.repository.CatalogRepository;
import com.oms.catalog.webClient.InventoryServiceWebClient;
import com.oms.inventory.dto.InventoryItemDTO;
import com.oms.inventory.entity.InventoryItemEntity;
import com.oms.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {

    Logger logger = LoggerFactory.getLogger(CatalogService.class);

    private CatalogRepository catalogRepository;
    private InventoryServiceWebClient inventoryServiceWebClient;

    public CatalogService(CatalogRepository catalogRepository, InventoryServiceWebClient inventoryServiceWebClient) {
        this.catalogRepository = catalogRepository;
        this.inventoryServiceWebClient = inventoryServiceWebClient;
    }

    public CatalogItemEntity addNewItem(CatalogItemEntity item) throws RuntimeException {
        Integer id = null;
        try {
            CatalogItemEntity returnedItem = catalogRepository.save(item);
            logger.info("New item successfully added to catalog db");
            id = returnedItem.getId();
            InventoryItemDTO inventoryItemDTO = new InventoryItemDTO(id, 0);
            try {
                inventoryServiceWebClient.createInventoryEntry(inventoryItemDTO).block();
            } catch (Exception ex) {
                logger.error("Error occurred while creating inventory entry", ex);
                logger.info("Rolling back catalog item creation due to inventory error");
                this.deleteById(id);
                throw new RuntimeException(ex);
            }
            return returnedItem;
        } catch (Exception e) {
            logger.error("Adding new item failed: ");
            e.printStackTrace();
            throw e;
        }
    }

    public CatalogItemEntity updateItem(int id, CatalogItemEntity item) throws RuntimeException {
        if (getItemById(id) == null) {
            logger.error(String.format("Failed to update item with id %s", id));
            return null;
        }
        item.setID(id);
        CatalogItemEntity returnedItem = catalogRepository.save(item);
        logger.info(String.format("Updated item with id %s", id));
        return returnedItem;
    }

    public List<CatalogItemEntity> getAllCatalogItems() {
        List<CatalogItemEntity> catalogItems = catalogRepository.findAll();
        for(CatalogItemEntity item: catalogItems){
            int id = item.getId();
            InventoryItemDTO inventoryItem = inventoryServiceWebClient.getInventoryItemById(id).block();
            if (inventoryItem != null) {
                item.setAvailableStock(inventoryItem.getQuantity());
            }
        }
        return catalogItems;
    }


    public CatalogItemEntity getItemById(int id) {
        CatalogItemEntity item = catalogRepository.findById(id).orElse(null);
        if (item == null) {
            logger.info(String.format("Could not find item with id: %s", id));
            return item;
        }
        logger.info(String.format("Found item with id ", id));
        return item;
    }

    public List<CatalogItemEntity> getItemsByCategory(String category) {
        List<CatalogItemEntity> itemsFound  = catalogRepository.findAllByCategory(category);
        return itemsFound;
    }

    public Boolean deleteById(int id) {
        if (getItemById(id) == null) {
            logger.error(String.format("No item found with id: %s", id));
            return false;
        } else {
            catalogRepository.deleteById(id);
            logger.info(String.format("Deleted item with id: %s", id));
            return true;
        }
    }

    public Boolean deleteAllItemsOfCategory(String category){
        if (getItemsByCategory(category).isEmpty()) {
            logger.error(String.format("Could not delete items with category: %s", category));
            return false;
        }

        catalogRepository.deleteAllByCategory(category);
        logger.info(String.format("Deleted items of category %s", category));
        return true;
    }
}
