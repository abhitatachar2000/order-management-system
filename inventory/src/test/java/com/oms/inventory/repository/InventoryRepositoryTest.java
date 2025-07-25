package com.oms.inventory.repository;

import com.oms.inventory.entity.InventoryItemEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@TestPropertySource(locations = "classpath:/application-test.properties")
public class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository repository;

    @AfterEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    void returnsEmptyListWhenNoItemsInInvetory() {
        List<InventoryItemEntity> allItems = repository.findAll();
        Assertions.assertTrue(allItems.isEmpty());
    }

    @Test
    void returnsElementsIfExist() {
        InventoryItemEntity itemOne = new InventoryItemEntity(1, 10);
        InventoryItemEntity itemTwo = new InventoryItemEntity(2, 20);
        repository.save(itemOne);
        repository.save(itemTwo);

        List<InventoryItemEntity> allItems = repository.findAll();
        Assertions.assertEquals(2, allItems.size());
        Assertions.assertEquals(1, allItems.get(0).getId());
        Assertions.assertEquals(2, allItems.get(1).getId());
    }

    @Test
    void returnsNullIfItemOfIdDoesNotExist() {
        InventoryItemEntity itemOne = new InventoryItemEntity(1, 100);
        InventoryItemEntity itemTwo = new InventoryItemEntity(2, 100);
        repository.save(itemOne);
        repository.save(itemTwo);

        Assertions.assertEquals(Optional.empty(), repository.findById(3));
    }

    @Test
    void returnsItemIfItemByIdExists() {
        InventoryItemEntity itemOne = new InventoryItemEntity(1, 20);
        InventoryItemEntity itemTwo = new InventoryItemEntity(2, 20);
        repository.save(itemOne);
        repository.save(itemTwo);

        InventoryItemEntity foundItem = repository.findById(1).orElse(null);
        Assertions.assertEquals(1, foundItem.getId());
    }

    @Test
    void deletesItemByIdIfExists() {
        InventoryItemEntity itemOne = new InventoryItemEntity(1, 20);
        InventoryItemEntity itemTwo = new InventoryItemEntity(2, 20);
        repository.save(itemOne);
        repository.save(itemTwo);

        repository.deleteById(1);
        List<InventoryItemEntity> allItems = repository.findAll();
        Assertions.assertEquals(1, allItems.size());
        Assertions.assertEquals(2, allItems.get(0).getId());
    }

    @Test
    void doesNothingIfDeletedItemThatDoesNotExist() {
        InventoryItemEntity itemOne = new InventoryItemEntity(1, 20);
        InventoryItemEntity itemTwo = new InventoryItemEntity(2, 20);
        repository.save(itemOne);
        repository.save(itemTwo);

        repository.deleteById(3);
        List<InventoryItemEntity> allItems = repository.findAll();
        Assertions.assertEquals(2, allItems.size());
        Assertions.assertEquals(1, allItems.get(0).getId());
        Assertions.assertEquals(2, allItems.get(1).getId());
    }

    @Test
    void updatesItemIfAlreadyExists() {
        InventoryItemEntity itemOne = new InventoryItemEntity(1, 20);
        InventoryItemEntity itemTwo = new InventoryItemEntity(2, 20);
        repository.save(itemOne);
        repository.save(itemTwo);

        InventoryItemEntity foundItemOne = repository.findById(1).orElse(null);
        Assertions.assertEquals(20, foundItemOne.getQuantity());

        InventoryItemEntity updatedItemOne = new InventoryItemEntity(1, 40);
        repository.save(updatedItemOne);
        foundItemOne = repository.findById(1).orElse(null);
        Assertions.assertEquals(40, foundItemOne.getQuantity());
    }
}
