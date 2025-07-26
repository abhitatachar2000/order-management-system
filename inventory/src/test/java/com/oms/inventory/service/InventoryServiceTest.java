package com.oms.inventory.service;

import com.oms.inventory.entity.InventoryItemEntity;
import com.oms.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@TestPropertySource(locations = "classpath:/application-test.properties")
public class InventoryServiceTest {

    @MockitoBean
    private InventoryRepository inventoryRepository;

    private InventoryService inventoryService;

    @BeforeEach
    void setup() {
        inventoryService = new InventoryService(inventoryRepository);
    }

    @Test
    void createsNewItemsInTheInvetory() throws RuntimeException {
        InventoryItemEntity itemOne = new InventoryItemEntity(1, 20);
        Mockito.doReturn(itemOne).when(inventoryRepository).save(any(InventoryItemEntity.class));

        InventoryItemEntity addedItem = inventoryService.addNewItem(itemOne);
        Assertions.assertEquals(itemOne.getId(), addedItem.getId());
    }

    @Test
    void throwsRuntimeExceptionIfAddingNewItemFails() {
        InventoryItemEntity itemOne = new InventoryItemEntity(1, 20);
        Mockito.doThrow(RuntimeException.class).when(inventoryRepository).save(any(InventoryItemEntity.class));

        Assertions.assertThrows(RuntimeException.class, () -> {
            inventoryService.addNewItem(itemOne);
        });
    }

    @Test
    void returnsEmptyListWhenNoItemsExist() {
        Mockito.doReturn(new ArrayList<>()).when(inventoryRepository).findAll();
        List<InventoryItemEntity> allItems = inventoryService.getAllItems();
        Assertions.assertTrue(allItems.isEmpty());
    }

    @Test
    void returnsAllItemsWhenItemsExist() {
        Mockito.doReturn(Arrays.asList(
                new InventoryItemEntity(1, 10),
                new InventoryItemEntity(2, 20),
                new InventoryItemEntity(3, 30)
        )).when(inventoryRepository).findAll();
        List<InventoryItemEntity> allItems = inventoryService.getAllItems();
        Assertions.assertFalse(allItems.isEmpty());
        Assertions.assertEquals(3, allItems.size());
    }

    @Test
    void throwsExceptionIfFindingAllItemsFail() throws RuntimeException {
        Mockito.doThrow(RuntimeException.class).when(inventoryRepository).findAll();
        Assertions.assertThrows(RuntimeException.class, () -> {
            inventoryService.getAllItems();
        });
    }

    @Test
    void findsItemForTheIdIfExists() throws RuntimeException {
        InventoryItemEntity itemOne = new InventoryItemEntity(1, 20);
        Mockito.doReturn(Optional.of(itemOne)).when(inventoryRepository).findById(1);
        InventoryItemEntity foundItem = inventoryService.findItemById(1);
        Assertions.assertEquals(itemOne.getId(), foundItem.getId());
    }

    @Test
    void returnsNullIfItemWithIdNotFound() throws RuntimeException {
        Mockito.doReturn(Optional.empty()).when(inventoryRepository).findById(1);
        InventoryItemEntity foundItem = inventoryService.findItemById(1);
        Assertions.assertNull(foundItem);
    }

    @Test
    void throwsExceptionIfFindByIdFails() throws RuntimeException {
        Mockito.doThrow(RuntimeException.class).when(inventoryRepository).findById(1);
        Assertions.assertThrows(RuntimeException.class, () -> {
            inventoryService.findItemById(1);
        });
    }

    @Test
    void deletesInventoryItemSuccessfully() throws RuntimeException {
        InventoryItemEntity itemOne = new InventoryItemEntity(1, 20);
        Mockito.doReturn(Optional.of(itemOne)).when(inventoryRepository).findById(1);
        Mockito.doNothing().when(inventoryRepository).deleteById(1);
        Boolean itemDeleted = inventoryService.deleteItemById(1);
        Assertions.assertTrue(itemDeleted);
    }

    @Test
    void doesNotDeleteItemIfItemDoesNotExist() throws RuntimeException {
        Mockito.doReturn(Optional.empty()).when(inventoryRepository).findById(1);
        Boolean itemDeletd = inventoryService.deleteItemById(1);
        Assertions.assertFalse(itemDeletd);
    }

    @Test
    void throwsExceptionIfItemDeletionFails() throws RuntimeException {
        InventoryItemEntity itemOne = new InventoryItemEntity(1, 20);
        Mockito.doReturn(Optional.of(itemOne)).when(inventoryRepository).findById(1);
        Mockito.doThrow(RuntimeException.class).when(inventoryRepository).deleteById(1);
        Assertions.assertThrows(RuntimeException.class, () -> {
            inventoryService.deleteItemById(1);
        });
    }

    @Test
    void updatesItemIfItemByIdExists() throws RuntimeException {
        InventoryItemEntity item = new InventoryItemEntity(1, 10);
        InventoryItemEntity updatedItem = new InventoryItemEntity(1, 20);
        Mockito.doReturn(Optional.of(item)).when(inventoryRepository).findById(1);
        Mockito.doReturn(updatedItem).when(inventoryRepository).save(any(InventoryItemEntity.class));
        InventoryItemEntity updatedReturnedItem = inventoryService.updateItem(updatedItem);
        Assertions.assertEquals(updatedItem.getId(), updatedReturnedItem.getId());
        Assertions.assertEquals(updatedItem.getQuantity(), updatedReturnedItem.getQuantity());
    }

    @Test
    void updateDoesNotHappenIfItemByIdDoesNotExist() throws RuntimeException {
        Mockito.doReturn(Optional.empty()).when(inventoryRepository).findById(1);
        InventoryItemEntity updatedItem = new InventoryItemEntity(1, 20);
        InventoryItemEntity updatedReturnedItem = inventoryService.updateItem(updatedItem);
        Assertions.assertNull(updatedReturnedItem);
    }

    @Test
    void updateThrowsErrorIfSaveFails() throws RuntimeException {
        InventoryItemEntity item = new InventoryItemEntity(1, 10);
        InventoryItemEntity updatedItem = new InventoryItemEntity(1, 20);
        Mockito.doReturn(Optional.of(item)).when(inventoryRepository).findById(1);
        Mockito.doThrow(RuntimeException.class).when(inventoryRepository).save(any(InventoryItemEntity.class));
        Assertions.assertThrows(RuntimeException.class, () -> {
            inventoryService.updateItem(updatedItem);
        });
    }
}
