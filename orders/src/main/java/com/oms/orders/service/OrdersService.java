package com.oms.orders.service;

import com.oms.orders.entity.OrderEntity;
import com.oms.orders.entity.OrderStatus;
import com.oms.orders.repository.OrdersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdersService {

    private OrdersRepository ordersRepository;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public OrderEntity createNewOrder(OrderEntity orderEntity) throws RuntimeException {
       try {
           return ordersRepository.save(orderEntity);
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
            if (!status.equals(OrderStatus.NEW ) &&
                    !status.equals(OrderStatus.PROCESSING) &&
                    !status.equals(OrderStatus.SHIPPED) &&
                    !status.equals(OrderStatus.OU_FOR_DELIVERY) &&
                    !status.equals(OrderStatus.DELIVERED) &&
                    !status.equals(OrderStatus.RETURN_PLACED) &&
                    !status.equals(OrderStatus.RETURNED) ) {
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
