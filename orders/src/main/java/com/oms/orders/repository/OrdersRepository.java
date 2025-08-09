package com.oms.orders.repository;

import com.oms.orders.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<OrderEntity, Integer> {

}
