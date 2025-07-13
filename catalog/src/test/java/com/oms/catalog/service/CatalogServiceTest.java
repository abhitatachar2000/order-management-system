package com.oms.catalog.service;

import com.oms.catalog.entity.CatalogItemEntity;
import com.oms.catalog.repository.CatalogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@TestPropertySource(locations = "classpath:/application-test.properties")
@SpringBootTest
public class CatalogServiceTest {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CatalogRepository catalogRepository;

    @AfterEach
    void tearDown() {
        catalogRepository.deleteAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoItemsExistInCatelog() {
        Assertions.assertTrue(catalogService.getAllCatalogItems().isEmpty());
    }

    @Test
    void shouldReturnListWithOneItemWhenSingleItemAddedToCatalog() {
        CatalogItemEntity newCatalogItem = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        catalogService.addNewItem(newCatalogItem);
        List<CatalogItemEntity> allCatalogItems = catalogService.getAllCatalogItems();
        Assertions.assertTrue(allCatalogItems.size() == 1);
        Assertions.assertEquals(newCatalogItem.getName(), allCatalogItems.get(0).getName());
    }
}
