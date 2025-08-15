package com.oms.orders.service;

import com.oms.orders.entity.OrderEntity;
import com.oms.orders.entity.OrderStatus;
import com.oms.orders.repository.OrdersRepository;
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
public class OrdersServiceTest {

    @MockitoBean
    private OrdersRepository ordersRepository;

    private OrdersService ordersService;

    @BeforeEach
    void setup() {
        ordersService = new OrdersService(ordersRepository);
    }

    @Test
    void createsNewOrderSuccessfully() {
        OrderEntity orderedItem = new OrderEntity(
                12,
                10,
                100.0d,
                1000.0d,
                OrderStatus.NEW,
                "testcontact@example.com"
        );
        orderedItem.setId(1);
        Mockito.doReturn(orderedItem).when(ordersRepository).save(any(OrderEntity.class));
        OrderEntity returnedOrder = ordersService.createNewOrder(orderedItem);
        Assertions.assertNotNull(returnedOrder);
        Assertions.assertEquals(1, returnedOrder.getId());
    }

    @Test
    void throwsRuntimeExceptionIfNewOrderCreationFails() {
        OrderEntity orderedItem = new OrderEntity(
                12,
                10,
                100.0d,
                1000.0d,
                OrderStatus.NEW,
                "testcontact@example.com"
        );
        Mockito.doThrow(RuntimeException.class).when(ordersRepository).save(any(OrderEntity.class));
        Assertions.assertThrows(RuntimeException.class, ()->{
            OrderEntity returnedOrder = ordersService.createNewOrder(orderedItem);
        });
    }

    // get all orders

    @Test
    void returnsAllExistingOrders() {
        List<OrderEntity> allOrders = Arrays.asList(
                new OrderEntity(1, 10, 100.0d, 1000.0d, OrderStatus.NEW, "test@example.com"),
                new OrderEntity(2, 20, 200.0d, 4000.0d, OrderStatus.SHIPPED, "test2@example.com")
        );
        Mockito.doReturn(allOrders).when(ordersRepository).findAll();
        List<OrderEntity> allReturnedOrders = ordersService.getAllOrders();
        Assertions.assertEquals(2, allReturnedOrders.size());
        Assertions.assertEquals(1, allReturnedOrders.get(0).getItemId());
        Assertions.assertEquals(2, allReturnedOrders.get(1).getItemId());
    }

    @Test
    void returnsEmptyListIfNoOrdersExists() {
        Mockito.doReturn(new ArrayList<OrderEntity>()).when(ordersRepository).findAll();
        List<OrderEntity> allReturnedOrders = ordersService.getAllOrders();
        Assertions.assertTrue(allReturnedOrders.isEmpty());
    }

    @Test
    void throwsExceptionIfFetchingAllItemFails() {
        Mockito.doThrow(RuntimeException.class).when(ordersRepository).findAll();
        Assertions.assertThrows(RuntimeException.class, ()->{
            ordersService.getAllOrders();
        });
    }

    // get orders by id

    @Test
    void returnsOrderIfOrderWithIdExists() {
        OrderEntity orderedItem = new OrderEntity(
                12,
                10,
                100.0d,
                1000.0d,
                OrderStatus.NEW,
                "testcontact@example.com"
        );
        orderedItem.setId(1);
        Mockito.doReturn(Optional.of(orderedItem)).when(ordersRepository).findById(1);
        OrderEntity foundItem = ordersService.getOrderById(1);
        Assertions.assertEquals(1, foundItem.getId());
    }


    @Test
    void returnsNullIfOrderWithIdDoesNotExist() {
        Mockito.doReturn(Optional.empty()).when(ordersRepository).findById(1);
        OrderEntity foundItem = ordersService.getOrderById(1);
        Assertions.assertNull(foundItem);
    }

    @Test
    void throwsExceptionIfFindingElementByIdFails() {
        Mockito.doThrow(RuntimeException.class).when(ordersRepository).findById(1);
        Assertions.assertThrows(RuntimeException.class, () -> {
            ordersService.getOrderById(1);
        });
    }

    // deletes order by id
    @Test
    void deletesOrderIfOrderWithIdExists() {
        OrderEntity orderedItem = new OrderEntity(
                12,
                10,
                100.0d,
                1000.0d,
                OrderStatus.NEW,
                "testcontact@example.com"
        );
        orderedItem.setId(1);
        Mockito.doReturn(Optional.of(orderedItem)).when(ordersRepository).findById(1);
        Boolean orderDeleted = ordersService.deleteOrderById(1);
        Assertions.assertTrue(orderDeleted);
    }

    @Test
    void doesNotDeleteOrderIfOrderWithIdNotFound() {
        Mockito.doReturn(Optional.empty()).when(ordersRepository).findById(1);
        Boolean orderDeleted = ordersService.deleteOrderById(1);
        Assertions.assertFalse(orderDeleted);
    }


    @Test
    void throwsExceptionIfDeletingElementByIdFails() {
        OrderEntity orderedItem = new OrderEntity(
                12,
                10,
                100.0d,
                1000.0d,
                OrderStatus.NEW,
                "testcontact@example.com"
        );
        orderedItem.setId(1);
        Mockito.doReturn(Optional.of(orderedItem)).when(ordersRepository).findById(1);
        Mockito.doThrow(RuntimeException.class).when(ordersRepository).deleteById(1);
        Assertions.assertThrows(RuntimeException.class, () -> {
            ordersService.deleteOrderById(1);
        });
    }

    @Test
    void updatesOrderIfOrderWithIdExists() {
        OrderEntity orderedItem = new OrderEntity(
                12,
                10,
                100.0d,
                1000.0d,
                OrderStatus.NEW,
                "testcontact@example.com"
        );
        orderedItem.setId(1);
        Mockito.doReturn(Optional.of(orderedItem)).when(ordersRepository).findById(1);
        OrderEntity updatedOrder = new OrderEntity(
                12,
                11,
                100.0d,
                1100.0d,
                OrderStatus.NEW,
                "testcontact@example.com"
        );
        updatedOrder.setId(1);
        Mockito.doReturn(updatedOrder).when(ordersRepository).save(any(OrderEntity.class));

        OrderEntity updatedOrderReturned = ordersService.updateOrder(1, updatedOrder);
        Assertions.assertEquals(1, updatedOrderReturned.getId());
        Assertions.assertEquals(1100d, updatedOrderReturned.getTotalPrice());
    }

    @Test
    void doesNotUpdateWhenOrderWithIdDoesNotExist() {
        Mockito.doReturn(Optional.empty()).when(ordersRepository).findById(1);
        OrderEntity updatedOrder = new OrderEntity(
                12,
                11,
                100.0d,
                1100.0d,
                OrderStatus.NEW,
                "testcontact@example.com"
        );
        OrderEntity updatedOrderReturned = ordersService.updateOrder(1, updatedOrder);
        Assertions.assertNull(updatedOrderReturned);
    }

    @Test
    void throwsExceptionIfUpdatingOrderFails() {
        OrderEntity orderedItem = new OrderEntity(
                12,
                10,
                100.0d,
                1000.0d,
                OrderStatus.NEW,
                "testcontact@example.com"
        );
        orderedItem.setId(1);
        Mockito.doReturn(Optional.of(orderedItem)).when(ordersRepository).findById(1);
        Mockito.doThrow(RuntimeException.class).when(ordersRepository).save(any(OrderEntity.class));
        Assertions.assertThrows(RuntimeException.class, () -> {
            ordersService.updateOrder(1, orderedItem);
        });
    }

    @Test
    void findsAllItemsWithSpecificStatus() {
        OrderEntity firstOrder = new OrderEntity(
                12,
                11,
                100.0d,
                1100.0d,
                OrderStatus.NEW,
                "testcontact@example.com"
        );
        firstOrder.setId(1);
        OrderEntity secondOrder = new OrderEntity(
                1,
                10,
                20.0d,
                200.0d,
                OrderStatus.NEW,
                "newTestContact@example.com"
        );
        secondOrder.setId(2);
        Mockito.doReturn(Arrays.asList(firstOrder, secondOrder)).when(ordersRepository).findByStatus(OrderStatus.NEW);
        List<OrderEntity> orders = ordersService.findAllOrderByStatus(OrderStatus.NEW);
        Assertions.assertEquals(2, orders.size());
        Assertions.assertEquals(1, orders.get(0).getId());
        Assertions.assertEquals(2, orders.get(1).getId());
    }

    @Test
    void throwsExceptionWhenInvalidStatusIsPassed() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            List<OrderEntity> orders = ordersService.findAllOrderByStatus("invalid");
        });
    }

    @Test
    void returnsEmptyListWhenNoOrdersForStatusExist() {
        Mockito.doReturn(new ArrayList<OrderEntity>()).when(ordersRepository).findByStatus(OrderStatus.NEW);
        List<OrderEntity> orders = ordersService.findAllOrderByStatus(OrderStatus.NEW);
        Assertions.assertTrue(orders.isEmpty());
    }

    @Test
    void throwsExceptionWhenFindingByStatusFails() {
        Mockito.doThrow(RuntimeException.class).when(ordersRepository).findByStatus(OrderStatus.NEW);
        Assertions.assertThrows(RuntimeException.class, () -> {
            List<OrderEntity> orders = ordersService.findAllOrderByStatus("invalid");
        });
    }
}
