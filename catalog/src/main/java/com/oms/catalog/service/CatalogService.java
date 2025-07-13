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

    @Autowired
    private CatalogRepository catalogRepository;

    public List<CatalogItemEntity> getAllCatalogItems() {
        return catalogRepository.findAll();
    }

    public void addNewItem(CatalogItemEntity item) {
        try {
            catalogRepository.save(item);
            logger.info("New item successfully added");
        } catch (Exception e) {
            logger.error("Adding new item failed: ");
            e.printStackTrace();
        }
    }
}
