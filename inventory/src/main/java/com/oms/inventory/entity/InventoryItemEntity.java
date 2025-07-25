package com.oms.inventory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class InventoryItemEntity {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "quantity")
    private int quantity;

    public InventoryItemEntity() {}

    public InventoryItemEntity(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
