package com.oms.catalog.repository;


import com.oms.catalog.entity.CatalogItemEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@TestPropertySource(locations = "classpath:/application-test.properties")
@SpringBootTest
public class CatalogRepositoryTest {

    @Autowired
    public CatalogRepository catalogRepository;

    @AfterEach
    void cleanUp() {
        catalogRepository.deleteAll();
    }

    @Test
    void savesNewItemToTheDB() {
        CatalogItemEntity newCatalogItem = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        CatalogItemEntity addedItem = catalogRepository.save(newCatalogItem);
        CatalogItemEntity retrievedItem = catalogRepository.findAll().get(0);

        Assertions.assertEquals(addedItem.getId(), retrievedItem.getId());
    }

    @Test
    void ifNoItemsExistReturnsEmptyList() {
        List<CatalogItemEntity> catalogItems = catalogRepository.findAll();
        Assertions.assertTrue(catalogItems.isEmpty());
    }

    @Test
    void findsAllItemsInTheCatalog() {
        CatalogItemEntity catalogItem1 = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        CatalogItemEntity catalogItem2 = new CatalogItemEntity(
                "item2",
                16.0,
                "category2",
                10
        );
        CatalogItemEntity addedItem1 = catalogRepository.save(catalogItem1);
        CatalogItemEntity addedItem2 = catalogRepository.save(catalogItem2);

        List<CatalogItemEntity> catalogItems = catalogRepository.findAll();

        Assertions.assertEquals(2, catalogItems.size());
        Assertions.assertEquals(addedItem1.getId(), catalogItems.get(0).getId());
        Assertions.assertEquals(addedItem2.getId(), catalogItems.get(1).getId());
    }

    @Test
    void findsElementById() {
        CatalogItemEntity catalogItem1 = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        CatalogItemEntity catalogItem2 = new CatalogItemEntity(
                "item2",
                16.0,
                "category2",
                10
        );
        CatalogItemEntity addedItem1 = catalogRepository.save(catalogItem1);
        CatalogItemEntity addedItem2 = catalogRepository.save(catalogItem2);

        CatalogItemEntity foundItem = catalogRepository.findById(addedItem1.getId()).orElse(null);


        Assertions.assertEquals(addedItem1.getId(), foundItem.getId());
    }

    @Test
    void ifItemDoesNotExistReturnsNull() {
        CatalogItemEntity catalogItem1 = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        CatalogItemEntity catalogItem2 = new CatalogItemEntity(
                "item2",
                16.0,
                "category2",
                10
        );
        CatalogItemEntity addedItem1 = catalogRepository.save(catalogItem1);
        CatalogItemEntity addedItem2 = catalogRepository.save(catalogItem2);

        CatalogItemEntity foundItem = catalogRepository.findById(999999).orElse(null);
        Assertions.assertEquals(null, foundItem);
    }

    @Test
    void findsAllItemsByCategory() {
        CatalogItemEntity catalogItem1 = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        CatalogItemEntity catalogItem2 = new CatalogItemEntity(
                "item2",
                16.0,
                "category2",
                10
        );
        CatalogItemEntity catalogItem3 = new CatalogItemEntity(
                "item3",
                19.67,
                "category1",
                5
        );

        CatalogItemEntity addedItem1 = catalogRepository.save(catalogItem1);
        CatalogItemEntity addedItem2 = catalogRepository.save(catalogItem2);
        CatalogItemEntity addedItem3 = catalogRepository.save(catalogItem3);

        List<CatalogItemEntity> foundItems = catalogRepository.findAllByCategory("category1");
        Assertions.assertEquals(2, foundItems.size());
        Assertions.assertEquals(addedItem1.getId(), foundItems.get(0).getId());
        Assertions.assertEquals(addedItem3.getId(), foundItems.get(1).getId());
    }

    @Test
    void returnsEmptyListIfNoItemsOfACategoryExist() {
        CatalogItemEntity catalogItem1 = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        CatalogItemEntity catalogItem2 = new CatalogItemEntity(
                "item2",
                16.0,
                "category2",
                10
        );
        CatalogItemEntity catalogItem3 = new CatalogItemEntity(
                "item3",
                19.67,
                "category1",
                5
        );

        CatalogItemEntity addedItem1 = catalogRepository.save(catalogItem1);
        CatalogItemEntity addedItem2 = catalogRepository.save(catalogItem2);
        CatalogItemEntity addedItem3 = catalogRepository.save(catalogItem3);

        List<CatalogItemEntity> foundItems = catalogRepository.findAllByCategory("category3");
        Assertions.assertEquals(0, foundItems.size());
    }

    @Test
    void deletesAllCatalogItems() {
        CatalogItemEntity catalogItem1 = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        CatalogItemEntity catalogItem2 = new CatalogItemEntity(
                "item2",
                16.0,
                "category2",
                10
        );
        CatalogItemEntity catalogItem3 = new CatalogItemEntity(
                "item3",
                19.67,
                "category3",
                5
        );

        CatalogItemEntity addedItem1 = catalogRepository.save(catalogItem1);
        CatalogItemEntity addedItem2 = catalogRepository.save(catalogItem2);
        CatalogItemEntity addedItem3 = catalogRepository.save(catalogItem3);

        Assertions.assertEquals(3, catalogRepository.findAll().size());
        catalogRepository.deleteAll();
        Assertions.assertEquals(0, catalogRepository.findAll().size());
    }

    @Test
    void deletesSpecficCatalogItem() {
        CatalogItemEntity catalogItem1 = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        CatalogItemEntity catalogItem2 = new CatalogItemEntity(
                "item2",
                16.0,
                "category2",
                10
        );
        CatalogItemEntity catalogItem3 = new CatalogItemEntity(
                "item3",
                19.67,
                "category3",
                5
        );

        CatalogItemEntity addedItem1 = catalogRepository.save(catalogItem1);
        CatalogItemEntity addedItem2 = catalogRepository.save(catalogItem2);
        CatalogItemEntity addedItem3 = catalogRepository.save(catalogItem3);

        Assertions.assertEquals(3, catalogRepository.findAll().size());
        catalogRepository.deleteById(addedItem3.getId());
        Assertions.assertEquals(2, catalogRepository.findAll().size());
    }

    @Test
    void deletesItemsOfASpecificCategory() {
        CatalogItemEntity catalogItem1 = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        CatalogItemEntity catalogItem2 = new CatalogItemEntity(
                "item2",
                16.0,
                "category2",
                10
        );
        CatalogItemEntity catalogItem3 = new CatalogItemEntity(
                "item3",
                19.67,
                "category1",
                5
        );

        CatalogItemEntity addedItem1 = catalogRepository.save(catalogItem1);
        CatalogItemEntity addedItem2 = catalogRepository.save(catalogItem2);
        CatalogItemEntity addedItem3 = catalogRepository.save(catalogItem3);

        Assertions.assertEquals(2, catalogRepository.findAllByCategory("category1").size());
        catalogRepository.deleteAllByCategory("category1");
        Assertions.assertEquals(0, catalogRepository.findAllByCategory("category1").size());
    }
}
