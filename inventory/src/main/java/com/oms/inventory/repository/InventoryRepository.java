package com.oms.inventory.repository;


import com.oms.inventory.entity.InventoryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<InventoryItemEntity, Integer> {

}
