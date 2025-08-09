package com.oms.orders.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="item_id")
    private int itemId;

    @Column(name="order_quantity")
    private int quantity;

    @Column(name = "price_per_unit")
    private double price_per_unit;

    @Column(name = "total_price")
    private double total_price;

    @Column(name = "order_status")
    private String status;

    @Column(name = "contact")
    private String contact;

    public OrderEntity() {}

    public OrderEntity(int itemId, int quantity, int price_per_unit, int total_price, String status, String contact) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.price_per_unit = price_per_unit;
        this.total_price = total_price;
        this.status = status;
        this.contact = contact;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice_per_unit() {
        return price_per_unit;
    }

    public void setPrice_per_unit(double price_per_unit) {
        this.price_per_unit = price_per_unit;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
