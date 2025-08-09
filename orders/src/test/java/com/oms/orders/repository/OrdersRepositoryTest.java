package com.oms.orders.repository;

import com.oms.orders.entity.OrderEntity;
import com.oms.orders.entity.OrderStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:/application-test.properties")
public class OrdersRepositoryTest {

    @Autowired
    private OrdersRepository ordersRepository;

    @AfterEach
    void cleanup() {
        ordersRepository.deleteAll();
    }

    @Test
    public void returnsEmptyListWhenNoItemsExist() {
        List<OrderEntity> allItems = ordersRepository.findAll();
        Assertions.assertEquals(0, allItems.size());
    }

    @Test
    public void addsNewItemToRepository() {
        OrderEntity orderEntityItem = new OrderEntity(
                1,
                10,
                20,
                200,
                OrderStatus.NEW,
                "test@example.com"
        );
        ordersRepository.save(orderEntityItem);
        List<OrderEntity> allOrders = ordersRepository.findAll();
        Assertions.assertEquals(1, allOrders.size());
        Assertions.assertEquals(1, allOrders.get(0).getItemId());
    }

    @Test
    public void returnsNullWhenNoOrderWithIdExists() {
        OrderEntity orderEntity = ordersRepository.findById(1).orElse(null);
        Assertions.assertNull(orderEntity);
    }

    @Test
    public void returnsItemWhenItemWithIdExists() {
        OrderEntity orderEntityItem = new OrderEntity(
                1,
                10,
                20,
                200,
                OrderStatus.NEW,
                "test@example.com"
        );
        ordersRepository.save(orderEntityItem);
        OrderEntity orderEntity = ordersRepository.findById(1).orElse(null);
        Assertions.assertNotNull(orderEntity);
    }

    @Test
    public void deleteById() {
        OrderEntity orderEntityItem = new OrderEntity(
                1,
                10,
                20,
                200,
                OrderStatus.NEW,
                "test@example.com"
        );
        ordersRepository.save(orderEntityItem);
        List<OrderEntity> allItems = ordersRepository.findAll();
        Assertions.assertEquals(1, allItems.size());
        ordersRepository.deleteById(allItems.get(0).getId());
        allItems = ordersRepository.findAll();
        Assertions.assertEquals(0, allItems.size());
    }

    @Test
    public void deletesAllItemsFromList() {
        OrderEntity orderOne = new OrderEntity(
                1,
                10,
                20,
                200,
                OrderStatus.NEW,
                "test@example.com"
        );
        OrderEntity orderTwo = new OrderEntity(
                2,
                10,
                2,
                20,
                OrderStatus.NEW,
                "test2@example.com"
        );

        ordersRepository.save(orderOne);
        ordersRepository.save(orderTwo);
        List<OrderEntity> allItems = ordersRepository.findAll();
        Assertions.assertEquals(2, allItems.size());
        ordersRepository.deleteAll();
        allItems = ordersRepository.findAll();
        Assertions.assertEquals(0, allItems.size());
    }

    @Test
    public void updatesExistingItems() {
        OrderEntity orderEntityItem = new OrderEntity(
                1,
                10,
                20,
                200,
                OrderStatus.NEW,
                "test@example.com"
        );
        ordersRepository.save(orderEntityItem);
        OrderEntity orderEntity = ordersRepository.findAll().get(0);
        OrderEntity updatedItem = new OrderEntity(
                1,
                10,
                22,
                220,
                OrderStatus.NEW,
                "test@example.com"
        );
        updatedItem.setId(orderEntity.getId());
        ordersRepository.save(updatedItem);
        orderEntity = ordersRepository.findAll().get(0);
        Assertions.assertNotNull(orderEntity);
        Assertions.assertEquals(22.0d, orderEntity.getPrice_per_unit());
    }
}

