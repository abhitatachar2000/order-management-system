package com.oms.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.inventory.dto.InventoryItemDTO;
import com.oms.orders.entity.OrderEntity;
import com.oms.orders.entity.OrderStatus;
import com.oms.orders.repository.OrdersRepository;
import com.oms.orders.webclient.InventoryServiceWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdersService {

    private OrdersRepository ordersRepository;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private InventoryServiceWebClient inventoryServiceWebClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    public OrdersService(OrdersRepository ordersRepository, InventoryServiceWebClient inventoryServiceWebClient) {
        this.ordersRepository = ordersRepository;
        this.inventoryServiceWebClient = inventoryServiceWebClient;
    }

    private Boolean isItemInStock(int itemId, int orderQuantity) {
        InventoryItemDTO inventoryItemDTO = inventoryServiceWebClient.getInventoryItemById(itemId).block();
        try {
            if (inventoryItemDTO.getQuantity() < orderQuantity) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateStock(OrderEntity updatedOrder) throws Exception {
        try {
            int itemID = updatedOrder.getItemId();
            int previousStock = inventoryServiceWebClient.getInventoryItemById(itemID).block().getQuantity();
            int orderedQuantity = updatedOrder.getQuantity();
            int newStock = previousStock - orderedQuantity;
            InventoryItemDTO updatedOrderDTO = new InventoryItemDTO(itemID, newStock);
            inventoryServiceWebClient.updateInventoryAfterOperation(updatedOrderDTO).block();
        } catch (Exception e) {
            logger.error(String.format("Order created but updating stock failed. Manually update the stock. Last Order: %s", objectMapper.writeValueAsString(updatedOrder)));
            e.printStackTrace();
            throw new RuntimeException(String.format("Order created but updating stock failed. Following error occurred: %s", e.getMessage()));
        }
    }

    private boolean isOrderStatusValid(String status) {
        return status.equals(OrderStatus.NEW) &&
                !status.equals(OrderStatus.PROCESSING) &&
                !status.equals(OrderStatus.SHIPPED) &&
                !status.equals(OrderStatus.OU_FOR_DELIVERY) &&
                !status.equals(OrderStatus.DELIVERED) &&
                !status.equals(OrderStatus.RETURN_PLACED) &&
                !status.equals(OrderStatus.RETURNED);
    }

    private void checkOrderValidity(OrderEntity orderEntity) throws RuntimeException {
        String status = orderEntity.getStatus();
        if (!isOrderStatusValid(status)) {
            throw new RuntimeException(String.format("Not a defined status: %s", status));
        }
        if (orderEntity.getQuantity() <= 0) {
            throw new RuntimeException("Order quantity should be at least 1");
        }
        if (!isItemInStock(orderEntity.getItemId(), orderEntity.getQuantity())) {
            throw new RuntimeException(String.format("Item with id %s not in stock", orderEntity.getItemId()));
        }
    }

    public OrderEntity createNewOrder(OrderEntity orderEntity) throws RuntimeException {
       try {
           checkOrderValidity(orderEntity);
           OrderEntity createdOrder = ordersRepository.save(orderEntity);
           updateStock(createdOrder);
           return createdOrder;
       } catch (Exception e) {
           logger.error("Creating new order failed. Following error occurred.");
           e.printStackTrace();
           throw new RuntimeException(e.getMessage());
       }
    }

    public List<OrderEntity> getAllOrders() throws RuntimeException {
        try {
            return ordersRepository.findAll();
        } catch (Exception e) {
            logger.error("Fetching all orders failed. Following error occurred.");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public OrderEntity getOrderById(int id) throws RuntimeException {
        try {
            return ordersRepository.findById(id).orElse(null);
        } catch (Exception e) {
            logger.error(String.format("Finding order with id %s failed. The following error occurred: "));
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public Boolean deleteOrderById(int id) throws RuntimeException {
        try {
            OrderEntity order =  ordersRepository.findById(id).orElse(null);
            if (order == null) {
                return false;
            }
            ordersRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            logger.error(String.format("Deleting order with id %s failed. The following error occurred: ", id));
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public OrderEntity updateOrder(int id, OrderEntity updatedEntity) throws RuntimeException {
        try {
            String status = updatedEntity.getStatus();
            if (!isOrderStatusValid(status)) {
                throw new RuntimeException(String.format("Not a defined status: %s", status));
            }
            OrderEntity order =  ordersRepository.findById(id).orElse(null);
            if (order == null) {
                logger.info(String.format("Update did not happen. Could not find order with id %s", id));
                return null;
            }
            updatedEntity.setId(id);
            return ordersRepository.save(updatedEntity);
        } catch (Exception e) {
            logger.error(String.format("Updating order with id %s failed. The following error occurred: ", id));
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    public List<OrderEntity> findAllOrderByStatus(String status) {
        try {
            if (!isOrderStatusValid(status)) {
                throw new RuntimeException(String.format("Not a defined status: %s", status));
            }
            List<OrderEntity> orders = ordersRepository.findByStatus(status);
            return orders;
        } catch (Exception e) {
            logger.error(String.format("Finding order by status %s. The following error occurred: ", status));
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
