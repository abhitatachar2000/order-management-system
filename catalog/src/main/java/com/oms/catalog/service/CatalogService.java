package com.oms.catalog.service;

import com.oms.catalog.entity.CatalogItemEntity;
import com.oms.catalog.repository.CatalogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {

    Logger logger = LoggerFactory.getLogger(CatalogService.class);

    private CatalogRepository catalogRepository;

    public CatalogService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public CatalogItemEntity addNewItem(CatalogItemEntity item) throws RuntimeException {
        try {
            CatalogItemEntity returnedItem = catalogRepository.save(item);
            logger.info("New item successfully added");
            return returnedItem;
        } catch (RuntimeException e) {
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
        return catalogRepository.findAll();
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
