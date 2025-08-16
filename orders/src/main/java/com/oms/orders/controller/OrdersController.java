package com.oms.orders.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.catalog.dto.CatalogDTO;
import com.oms.catalog.entity.CatalogItemEntity;
import com.oms.orders.dto.OrderDTO;
import com.oms.orders.entity.OrderEntity;
import com.oms.orders.service.OrdersService;
import com.oms.orders.webclient.CatalogServiceWebClient;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {

    Logger logger = LoggerFactory.getLogger(getClass());

    private OrdersService ordersService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private CatalogServiceWebClient catalogServiceWebClient;

    @Autowired
    public OrdersController(OrdersService ordersService, CatalogServiceWebClient catalogServiceWebClient) {
        this.ordersService = ordersService;
        this.catalogServiceWebClient = catalogServiceWebClient;
    }

    @PostMapping
    public ResponseEntity<?> createNewOrder(@RequestBody OrderDTO orderDTO) throws JsonProcessingException {
        logger.info(String.format("Received request to create new order: %s", objectMapper.writeValueAsString(orderDTO)));
        try {
            OrderEntity orderEntity = convertDtoToEntity(orderDTO);
            OrderEntity createdOrder = ordersService.createNewOrder(orderEntity);
            logger.info(String.format("Created new order with id: %s", createdOrder.getId()));
            return ResponseEntity.status(HttpStatus.CREATED).body(convertEntityToDto(createdOrder));
        } catch (Exception e) {
            logger.info(String.format("Failed to create new order. Following exception occurred: %s", e.getMessage()));
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        logger.info("Received request to fetch all orders");
        try {
            List<OrderEntity> allOrders = ordersService.getAllOrders();
            List<OrderDTO> allOrdersPayload = allOrders.stream().map(this::convertEntityToDto).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(allOrdersPayload);
        } catch (Exception e) {
            logger.info(String.format("Failed to create new order. Following exception occurred: %s", e.getMessage()));
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderWithId(@PathVariable("id") int id) {
        logger.info(String.format("Received request to fetch order with id %s", id));
        try {
            OrderEntity orderFound = ordersService.getOrderById(id);
            if (orderFound == null) {
                logger.info(String.format("No order found with id %s", id));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Order with id %s not found", id));
            }
            return ResponseEntity.status(HttpStatus.OK).body(convertEntityToDto(orderFound));
        } catch (Exception e) {
            logger.info(String.format("Failed to create new order. Following exception occurred: %s", e.getMessage()));
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable("id") int id, @RequestBody OrderDTO orderDTO) {
        logger.info(String.format("Received request to update order with id %s", id));
        try {
            OrderEntity orderEntity = convertDtoToEntity(orderDTO);
            orderEntity.setId(id);
            OrderEntity updatedOrder = ordersService.updateOrder(id, orderEntity);
            if (updatedOrder == null) {
                logger.info(String.format("No order found with id %s", id));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Order with id %s not found", id));
            }
            return ResponseEntity.status(HttpStatus.OK).body(convertEntityToDto(updatedOrder));
        } catch (Exception e) {
            logger.info(String.format("Failed to create new order. Following exception occurred: %s", e.getMessage()));
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") int id) {
        logger.info(String.format("Received request to delete order with id %s", id));
        try {
            Boolean orderDeleted = ordersService.deleteOrderById(id);
            if (!orderDeleted) {
                logger.info(String.format("No order found with id %s", id));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Order with id %s not found", id));
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            logger.info(String.format("Failed to create new order. Following exception occurred: %s", e.getMessage()));
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(params = "status")
    public ResponseEntity<?> getOrdersOfStatus(@RequestParam("status") String status) {
        logger.info(String.format("Received request to find all orders with status \'%s\'", status));
        try {
            List<OrderEntity> orders = ordersService.findAllOrderByStatus(status);
            List<OrderDTO> allOrdersPayload = orders.stream().map(this::convertEntityToDto).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(orders);
        } catch (Exception e) {
            logger.info(String.format("Failed to create new order. Following exception occurred: %s", e.getMessage()));
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    OrderEntity convertDtoToEntity(OrderDTO orderDTO) {
        try {
            CatalogDTO catalogItem = catalogServiceWebClient.getCatalogItem(orderDTO.getItemId()).block();
            double pricePerUnit = catalogItem.getPricePerUnit();
            double totalPrice = orderDTO.getQuantity() * pricePerUnit;
            return new OrderEntity(
                    orderDTO.getItemId(),
                    orderDTO.getQuantity(),
                    pricePerUnit,
                    totalPrice,
                    orderDTO.getStatus(),
                    orderDTO.getContact()
            );
        } catch (Exception e) {
            throw new RuntimeException(String.format("Could not convert DTO to Entity. Following error occurred: %s", e.getMessage()));
        }

    }


    OrderDTO convertEntityToDto(OrderEntity orderEntity) {
        OrderDTO orderDTO = new OrderDTO(
                orderEntity.getItemId(),
                orderEntity.getQuantity(),
                orderEntity.getStatus(),
                orderEntity.getContact()
        );
        orderDTO.setId(orderEntity.getId());
        orderDTO.setPricePerUnit(orderEntity.getPricePerUnit());
        orderDTO.setTotalPrice(orderDTO.getTotalPrice());
        return orderDTO;
    }
}
