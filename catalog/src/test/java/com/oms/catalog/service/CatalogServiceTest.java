package com.oms.catalog.service;

import com.oms.catalog.entity.CatalogItemEntity;
import com.oms.catalog.repository.CatalogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@TestPropertySource(locations = "classpath:/application-test.properties")
@SpringBootTest
public class CatalogServiceTest {

    @MockitoBean
    private CatalogRepository catalogRepository;

    private CatalogService catalogService;

    @BeforeEach
    void setup() {
        this.catalogService = new CatalogService(catalogRepository);
    }

    @Test
    void shouldSaveNewItemSuccessfully() throws Exception {
        CatalogItemEntity itemOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        Mockito.doReturn(itemOne).when(catalogRepository).save(any(CatalogItemEntity.class));
        Assertions.assertEquals(itemOne.getName(), catalogService.addNewItem(itemOne).getName());
    }

    @Test
    void shouldThrowExceptionWhenItemAdditionFails() throws Exception {
        CatalogItemEntity itemOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        Mockito.doThrow(RuntimeException.class).when(catalogRepository).save(any(CatalogItemEntity.class));
        Assertions.assertThrows(RuntimeException.class, () -> {catalogService.addNewItem(itemOne);});
    }

    @Test
    void shouldReturnEmptyListWhenNoItemsExistInCatelog() {
        ArrayList emptyList = new ArrayList();
        Mockito.doReturn(emptyList).when(catalogRepository).findAll();
        Assertions.assertTrue(catalogService.getAllCatalogItems().isEmpty());
    }

    @Test
    void shoudReturnListOfAllExistingItems() {
        CatalogItemEntity itemOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        CatalogItemEntity itemTwo = new CatalogItemEntity(
                "item2",
                16.00,
                "category2",
                15
        );
        CatalogItemEntity itemThree = new CatalogItemEntity(
                "item3",
                14.50,
                "category2",
                5
        );

        List<CatalogItemEntity> allItems = Arrays.asList(itemOne, itemTwo, itemThree);
        Mockito.doReturn(allItems).when(catalogRepository).findAll();
        Assertions.assertEquals(3, catalogService.getAllCatalogItems().size());
    }

    @Test
    void shouldFindItemWithIdWhenTheITemExists() {
        CatalogItemEntity itemEntityOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );

        Mockito.doReturn(Optional.of(itemEntityOne)).when(catalogRepository).findById(1);

        CatalogItemEntity addedItemOne = catalogService.addNewItem(itemEntityOne);

        CatalogItemEntity foundItem = catalogService.getItemById(1);

        Assertions.assertEquals(itemEntityOne.getName(), foundItem.getName());
    }

    @Test
    void shoudlReturnNullIfNoElementWithIdExists() {
        Mockito.doReturn(Optional.empty()).when(catalogRepository).findById(2);
        CatalogItemEntity foundItem = catalogService.getItemById(2);
        Assertions.assertNull(foundItem);
    }

    @Test
    void shouldReturnListOfItemsOfAParticularCategoryIfExists() {
        CatalogItemEntity itemEntityOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        CatalogItemEntity itemEntityTwo = new CatalogItemEntity(
                "item2",
                14.20,
                "category1",
                5
        );

        Mockito.doReturn(Arrays.asList(itemEntityOne, itemEntityTwo))
                .when(catalogRepository).findAllByCategory("category1");

        List<CatalogItemEntity> items = catalogService.getItemsByCategory("category1");

        Assertions.assertEquals(2, items.size());

        Assertions.assertEquals("category1", items.get(0).getCategory());
        Assertions.assertEquals("category1", items.get(0).getCategory());
    }

    @Test
    void shouldReturnEmptyListIfItemsOfCategoryNotFound() {
        Mockito.doReturn(new ArrayList<CatalogItemEntity>())
                .when(catalogRepository).findAllByCategory("category1");
        List<CatalogItemEntity> items = catalogService.getItemsByCategory("category1");
        Assertions.assertTrue(items.isEmpty());
    }

    @Test
    void shouldDeleteItemsIfExists() {
        CatalogItemEntity itemEntityOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        Mockito.doReturn(Optional.of(itemEntityOne)).when(catalogRepository).findById(1);
        Mockito.doNothing().when(catalogRepository).deleteById(1);
        Boolean itemDeleted = catalogService.deleteById(1);
        Assertions.assertTrue(itemDeleted);
    }

    @Test
    void shouldNotDeleteItemIdDoesNotExist() {
        Mockito.doReturn(Optional.empty()).when(catalogRepository).findById(1);
        Boolean itemDeleted = catalogService.deleteById(1);
        Assertions.assertTrue(itemDeleted == false);
    }

    @Test
    void shouldDeleteAllItemsByCategory() {
        CatalogItemEntity itemEntityOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1",
                10
        );
        Mockito.doReturn(Arrays.asList(itemEntityOne)).when(catalogRepository).findAllByCategory("category1");
        Mockito.doNothing().when(catalogRepository).deleteAllByCategory("category1");
        Boolean deletedAllItemsOfCategory = catalogService.deleteAllItemsOfCategory("category1");
        Assertions.assertTrue(deletedAllItemsOfCategory);
    }

    @Test
    void shouldNotDeleteItemsIfCategoryDoesNotExist() {
        Mockito.doReturn(new ArrayList<CatalogItemEntity>()).when(catalogRepository).findAllByCategory("category1");
        Boolean deletedAllItemsOfCategory = catalogService.deleteAllItemsOfCategory("category1");
        Assertions.assertFalse(deletedAllItemsOfCategory);
    }
}
