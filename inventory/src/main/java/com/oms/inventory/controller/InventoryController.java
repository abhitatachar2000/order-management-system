package com.oms.inventory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    // dummy implementation
    @GetMapping("/items")
    public ArrayList getAllItems() {
        return new ArrayList<>();
    }
}
