package com.oms.catalog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "catelog_items")
public class CatalogItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "price_per_unit")
    private Double pricePerUnit;

    @Column(name = "category")
    private String category;

    @Transient
    private int availableStock;

    public CatalogItemEntity() {}

    public CatalogItemEntity(String name, Double pricePerUnit, String category) {
        this.name = name;
        this.pricePerUnit = pricePerUnit;
        this.category = category;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }
}
