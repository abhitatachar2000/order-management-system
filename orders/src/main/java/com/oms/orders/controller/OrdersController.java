package com.oms.orders.controller;

import com.oms.orders.dto.OrderDTO;
import com.oms.orders.entity.OrderEntity;
import com.oms.orders.service.OrdersService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {

    private OrdersService ordersService;

    @Autowired
    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PostMapping
    public ResponseEntity<?> createNewOrder(@RequestBody OrderDTO orderDTO) {
        try {
            OrderEntity orderEntity = convertDtoToEntity(orderDTO);
            OrderEntity createdOrder = ordersService.createNewOrder(orderEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            List<OrderEntity> allOrders = ordersService.getAllOrders();
            List<OrderDTO> allOrdersPayload = allOrders.stream().map(this::convertEntityToDto).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(allOrdersPayload);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderWithId(@PathVariable("id") int id) {
        try {
            OrderEntity orderFound = ordersService.getOrderById(id);
            if (orderFound == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Order with id %s not found", id));
            }
            return ResponseEntity.status(HttpStatus.OK).body(convertEntityToDto(orderFound));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable("id") int id, @RequestBody OrderDTO orderDTO) {
        try {
            OrderEntity orderEntity = convertDtoToEntity(orderDTO);
            orderEntity.setId(id);
            OrderEntity updatedOrder = ordersService.updateOrder(id, orderEntity);
            if (updatedOrder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Order with id %s not found", id));
            }
            return ResponseEntity.status(HttpStatus.OK).body(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") int id) {
        try {
            Boolean orderDeleted = ordersService.deleteOrderById(id);
            if (!orderDeleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Order with id %s not found", id));
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(params = "status")
    public ResponseEntity<?> getOrdersOfStatus(@RequestParam("status") String status) {
        try {
            List<OrderEntity> orders = ordersService.findAllOrderByStatus(status);
            return ResponseEntity.status(HttpStatus.OK).body(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }




    OrderEntity convertDtoToEntity(OrderDTO orderDTO) {
        return new OrderEntity(
                orderDTO.getItemId(),
                orderDTO.getQuantity(),
                orderDTO.getPricePerUnit(),
                orderDTO.getTotalPrice(),
                orderDTO.getStatus(),
                orderDTO.getContact()
        );
    }


    OrderDTO convertEntityToDto(OrderEntity orderEntity) {
        OrderDTO orderDTO = new OrderDTO(
                orderEntity.getItemId(),
                orderEntity.getQuantity(),
                orderEntity.getPricePerUnit(),
                orderEntity.getTotalPrice(),
                orderEntity.getStatus(),
                orderEntity.getContact()
        );
        orderDTO.setId(orderEntity.getId());
        return orderDTO;
    }
}
