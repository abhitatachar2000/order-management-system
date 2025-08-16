package com.oms.orders.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.catalog.dto.CatalogDTO;
import com.oms.orders.dto.OrderDTO;
import com.oms.orders.entity.OrderEntity;
import com.oms.orders.entity.OrderStatus;
import com.oms.orders.service.OrdersService;
import com.oms.orders.webclient.CatalogServiceWebClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdersController.class)
@TestPropertySource(locations = "classpath:/application-test.properties")
public class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrdersService ordersService;

    @MockitoBean
    private CatalogServiceWebClient catalogServiceWebClient;

    @Test
    void return201OnNewOrderCreation() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1,
                10,
                OrderStatus.NEW,
                "test@example.com");

        CatalogDTO catalogDTO = new CatalogDTO("someProduct", 20d, "Category2");

        OrderEntity orderEntity = new OrderEntity(1,
                10,
                20d,
                200d,
                OrderStatus.NEW,
                "test@example.com");
        orderEntity.setId(1);

        Mockito.doReturn(Mono.just(catalogDTO)).when(catalogServiceWebClient).getCatalogItem(1);
        Mockito.doReturn(orderEntity).when(ordersService).createNewOrder(any(OrderEntity.class));
        String jsonString = objectMapper.writeValueAsString(orderDTO);
        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isCreated());
    }

    @Test
    void returnInternalServerErrorIfAddingNewOrderFails() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1,
                10,
                OrderStatus.NEW,
                "test@example.com");

        Mockito.doThrow(RuntimeException.class).when(ordersService).createNewOrder(any(OrderEntity.class));
        String jsonString = objectMapper.writeValueAsString(orderDTO);
        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void successfullyFetchesAllExistingOrders() throws Exception {
        OrderEntity orderOne = new OrderEntity(1,
                10,
                20d,
                200d,
                OrderStatus.NEW,
                "test@example.com");
        orderOne.setId(1);

        OrderEntity orderTwo = new OrderEntity(1,
                10,
                30d,
                300d,
                OrderStatus.DELIVERED,
                "testNew@example.com");
        orderTwo.setId(2);

        Mockito.doReturn(Arrays.asList(orderOne, orderTwo)).when(ordersService).getAllOrders();

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void returnsInternalServerErrorIfFetchingItemsFails() throws Exception {
        Mockito.doThrow(RuntimeException.class).when(ordersService).getAllOrders();
        mockMvc.perform(get("/api/v1/orders")).andExpect(status().isInternalServerError());
    }

    @Test
    void findsItemWithIdIfExists() throws Exception {
        OrderEntity orderOne = new OrderEntity(1,
                10,
                20d,
                200d,
                OrderStatus.NEW,
                "test@example.com");
        orderOne.setId(1);
        Mockito.doReturn(orderOne).when(ordersService).getOrderById(1);

        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void returnsNotFoundIfOrderWithIdIsNotFound() throws Exception {
        Mockito.doReturn(null).when(ordersService).getOrderById(1);
        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsInternalServerErrorIfFindingElementByIdFails() throws Exception {
        Mockito.doThrow(RuntimeException.class).when(ordersService).getOrderById(1);
        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void sucessfullyUpdatesTheExistingOrder() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1,
                10,
                OrderStatus.NEW,
                "test@example.com");

        CatalogDTO catalogDTO = new CatalogDTO(
                "item",
                20d,
                "category1"
        );

        OrderEntity orderEntity = new OrderEntity(1,
                10,
                20d,
                200d,
                OrderStatus.DELIVERED,
                "test@example.com");
        orderEntity.setId(1);

        Mockito.doReturn(Mono.just(catalogDTO)).when(catalogServiceWebClient).getCatalogItem(1);
        Mockito.doReturn(orderEntity).when(ordersService).updateOrder(eq(1), any(OrderEntity.class));
        mockMvc.perform(put("/api/v1/orders/1").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERED));

    }

    @Test
    void returnsNotFoundIfOrderWithIdDoesNotExist() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1,
                10,
                OrderStatus.NEW,
                "test@example.com");
        CatalogDTO catalogDTO = new CatalogDTO(
                "item",
                20d,
                "category1"
        );
        Mockito.doReturn(Mono.just(catalogDTO)).when(catalogServiceWebClient).getCatalogItem(1);
        Mockito.doReturn(null).when(ordersService).updateOrder(eq(1), any(OrderEntity.class));
        mockMvc.perform(put("/api/v1/orders/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void throwsInternalServerErrorIfUpdatingOrderFails() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1,
                10,
                OrderStatus.NEW,
                "test@example.com");
        Mockito.doThrow(RuntimeException.class).when(ordersService).updateOrder(eq(1), any(OrderEntity.class));
        mockMvc.perform(put("/api/v1/orders/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isInternalServerError());

    }

    @Test
    void sucessfullyDeletesTheExistingOrder() throws Exception {

        Mockito.doReturn(true).when(ordersService).deleteOrderById(1);
        mockMvc.perform(delete("/api/v1/orders/1"))
                .andExpect(status().isOk());

    }

    @Test
    void returnsNotFoundIfDeletingNonExistingOrder() throws Exception {
        Mockito.doReturn(false).when(ordersService).deleteOrderById(1);
        mockMvc.perform(delete("/api/v1/orders/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void throwsInternalServerErrorIfDeletingOrderFails() throws Exception {
        Mockito.doThrow(RuntimeException.class).when(ordersService).deleteOrderById(1);
        mockMvc.perform(delete("/api/v1/orders/1"))
                .andExpect(status().isInternalServerError());

    }

    @Test
    void sucessfullyFindsAllOrdersWithSpecifcStatus() throws Exception {
        OrderEntity orderOne = new OrderEntity(1,
                10,
                20d,
                200d,
                OrderStatus.NEW,
                "test@example.com");

        OrderEntity orderTwo = new OrderEntity(1,
                10,
                20d,
                200d,
                OrderStatus.NEW,
                "testTwo@example.com");

        Mockito.doReturn(Arrays.asList(orderOne, orderTwo)).when(ordersService).findAllOrderByStatus(OrderStatus.NEW);

        mockMvc.perform(get("/api/v1/orders?status=new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("new"))
                .andExpect(jsonPath("$[1].status").value("new"));
    }

    @Test
    void returnsEmptyListWhenWhenNoOrdersOfStatusExists() throws Exception {
        Mockito.doReturn(new ArrayList<OrderEntity>()).when(ordersService).findAllOrderByStatus(OrderStatus.NEW);
        mockMvc.perform(get("/api/v1/orders?status=new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void returnsInternalServerErrorWhenFetchingOrderByStatusFails() throws Exception {
        Mockito.doThrow(RuntimeException.class).when(ordersService).findAllOrderByStatus(OrderStatus.NEW);
        mockMvc.perform(get("/api/v1/orders?status=new"))
                .andExpect(status().isInternalServerError());
    }
}
