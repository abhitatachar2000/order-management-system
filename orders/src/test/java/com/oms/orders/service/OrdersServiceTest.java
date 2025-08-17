package com.oms.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oms.inventory.dto.InventoryItemDTO;
import com.oms.orders.entity.OrderEntity;
import com.oms.orders.entity.OrderStatus;
import com.oms.orders.repository.OrdersRepository;
import com.oms.orders.webclient.InventoryServiceWebClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    @MockitoBean
    private InventoryServiceWebClient inventoryServiceWebClient;

    private OrdersService ordersService;

    @BeforeEach
    void setup() {
        ordersService = new OrdersService(ordersRepository, inventoryServiceWebClient);
    }

    @Test
    void createsNewOrderSuccessfully() throws JsonProcessingException {
        OrderEntity orderedItem = new OrderEntity(
                12,
                10,
                100.0d,
                1000.0d,
                OrderStatus.DELIVERED,
                "testcontact@example.com"
        );
        InventoryItemDTO inventoryItemDTO = new InventoryItemDTO(12, 20);
        orderedItem.setId(1);
        Mockito.doReturn(Mono.just(inventoryItemDTO)).when(inventoryServiceWebClient).getInventoryItemById(12);
        Mockito.doReturn(orderedItem).when(ordersRepository).save(any(OrderEntity.class));
        Mockito.doReturn(Mono.empty()).when(inventoryServiceWebClient).updateInventoryAfterOperation(any(InventoryItemDTO.class));
        OrderEntity returnedOrder = ordersService.createNewOrder(orderedItem);
        Assertions.assertNotNull(returnedOrder);
        Assertions.assertEquals(1, returnedOrder.getId());
        Assertions.assertEquals(OrderStatus.NEW, returnedOrder.getStatus());
    }

    @Test
    void cannotCreateOrderWithInvalidStatus() {
        OrderEntity orderedItem = new OrderEntity(
                12,
                10,
                100.0d,
                1000.0d,
                "invalid",
                "testcontact@example.com"
        );
        Assertions.assertThrows(RuntimeException.class, ()->{
            OrderEntity returnedOrder = ordersService.createNewOrder(orderedItem);
        });
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

    @Test
    void doesNotCreateOrderWhenItemNotInStock() {
        OrderEntity orderedItem = new OrderEntity(
                12,
                10,
                100.0d,
                1000.0d,
                OrderStatus.NEW,
                "testcontact@example.com"
        );
        InventoryItemDTO inventoryItemDTO = new InventoryItemDTO(12, 5);
        orderedItem.setId(1);
        Mockito.doReturn(Mono.just(inventoryItemDTO)).when(inventoryServiceWebClient).getInventoryItemById(12);
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
    void updatesOrderIfOrderExistsAndOnlyStatusChanges() {
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
                10,
                100.0d,
                1000.0d,
                OrderStatus.SHIPPED,
                "testcontact@example.com"
        );
        updatedOrder.setId(1);
        Mockito.doReturn(updatedOrder).when(ordersRepository).save(any(OrderEntity.class));

        OrderEntity updatedOrderReturned = ordersService.updateOrder(1, updatedOrder);
        Assertions.assertEquals(1, updatedOrderReturned.getId());
        Assertions.assertEquals(OrderStatus.SHIPPED, updatedOrderReturned.getStatus());
    }

    @Test
    void doesNotUpdateOrderIfAnythingElseChangesOtherThanStatus() {
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
        Assertions.assertThrows(RuntimeException.class, () -> {
            ordersService.updateOrder(1, updatedOrder);
        });
    }

    @Test
    void doesNotUpdateIfOrderStatusDoesNotChange() {
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
                10,
                100.0d,
                1000.00d,
                OrderStatus.NEW,
                "testcontact@example.com"
        );
        updatedOrder.setId(1);
        Assertions.assertThrows(RuntimeException.class, () -> {
            ordersService.updateOrder(1, updatedOrder);
        });
    }

    @Test
    void doesNotUpdateForInvalidOrderStatus() {
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
                10,
                100.0d,
                1000.00d,
                "invalid",
                "testcontact@example.com"
        );
        updatedOrder.setId(1);
        Assertions.assertThrows(RuntimeException.class, () -> {
            ordersService.updateOrder(1, updatedOrder);
        });
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
        OrderEntity updatedOrder = new OrderEntity(
                12,
                10,
                100.0d,
                1000.0d,
                OrderStatus.SHIPPED,
                "testcontact@example.com"
        );
        orderedItem.setId(1);
        Mockito.doReturn(Optional.of(orderedItem)).when(ordersRepository).findById(1);
        Mockito.doThrow(RuntimeException.class).when(ordersRepository).save(any(OrderEntity.class));
        Assertions.assertThrows(RuntimeException.class, () -> {
            ordersService.updateOrder(1, updatedOrder);
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
