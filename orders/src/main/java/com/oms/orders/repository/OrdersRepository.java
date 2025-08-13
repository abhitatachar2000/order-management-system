package com.oms.orders.repository;

import com.oms.orders.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<OrderEntity, Integer> {
    public List<OrderEntity> findByStatus(String status);
}
