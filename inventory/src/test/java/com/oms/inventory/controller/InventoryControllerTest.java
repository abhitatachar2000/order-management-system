package com.oms.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.inventory.dto.InventoryItemDTO;
import com.oms.inventory.entity.InventoryItemEntity;
import com.oms.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.swing.text.html.parser.Entity;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;

@WebMvcTest(InventoryController.class)
@TestPropertySource(locations = "classpath:/application-test.properties")
public class InventoryControllerTest {

    @MockitoBean
    private InventoryService inventoryService;

    private InventoryController inventoryController;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        inventoryController = new InventoryController(inventoryService);
    }

    @Test
    void returnsEmptyArrayWhenNoElementsArePresent() throws Exception {
        Mockito.doReturn(new ArrayList<InventoryItemEntity>()).when(inventoryService).getAllItems();
        mockMvc.perform(get("/api/v1/inventory/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void returnsAllItemsIfItemsArePresent() throws Exception {
        Mockito.doReturn(Arrays.asList(
                new InventoryItemEntity(1, 19),
                new InventoryItemEntity(2, 25)
        )).when(inventoryService).getAllItems();
        mockMvc.perform(get("/api/v1/inventory/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void returnsA5xxErrorIfFindingElementsFails() throws Exception {
        Mockito.doThrow(RuntimeException.class).when(inventoryService).getAllItems();
        mockMvc.perform(get("/api/v1/inventory/items")).andExpect(status().isInternalServerError());
    }

    @Test
    void createsNewItem() throws Exception {
        InventoryItemDTO newItemDTO = new InventoryItemDTO(1, 20);
        Mockito.doReturn(null).when(inventoryService).findItemById(1);

        mockMvc.perform(post("/api/v1/inventory/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(newItemDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void createNewItemReturnsConflictIfItemAlreadyExistsWithSameId() throws Exception {
        InventoryItemDTO newItemDTO = new InventoryItemDTO(1, 20);
        InventoryItemEntity existingItem = new InventoryItemEntity(1, 10);
        Mockito.doReturn(existingItem).when(inventoryService).findItemById(1);

        mockMvc.perform(post("/api/v1/inventory/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newItemDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void createNewItemThrows5xxIfCretingNewItemFails() throws Exception {
        InventoryItemDTO newItemDTO = new InventoryItemDTO(1, 20);
        InventoryItemEntity existingItem = new InventoryItemEntity(1, 10);
        Mockito.doThrow(RuntimeException.class).when(inventoryService).findItemById(1);

        mockMvc.perform(post("/api/v1/inventory/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newItemDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void findsItemByIdIfExists() throws Exception {
        InventoryItemEntity itemOne = new InventoryItemEntity(1, 10);
        Mockito.doReturn(itemOne).when(inventoryService).findItemById(1);
        mockMvc.perform(get("/api/v1/inventory/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void returnsA4xxErrorIfItemWithIdDoesNotExist() throws Exception {
        Mockito.doReturn(null).when(inventoryService).findItemById(1);
        mockMvc.perform(get("/api/v1/inventory/items/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsA5xxErrorIfFindingElementByIdFails() throws Exception {
        Mockito.doThrow(RuntimeException.class).when(inventoryService).findItemById(1);
        mockMvc.perform(get("/api/v1/inventory/items/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deletesItemsSuccessfully() throws Exception {
        Mockito.doReturn(true).when(inventoryService).deleteItemById(1);
        mockMvc.perform(delete("/api/v1/inventory/items/item?id=1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItemThrows4xxWhenItemDoesNotExist() throws Exception {
        Mockito.doReturn(false).when(inventoryService).deleteItemById(1);
        mockMvc.perform(delete("/api/v1/inventory/items/item?id=1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void throws5xxWhenItemDeletionFails() throws Exception {
        Mockito.doThrow(RuntimeException.class).when(inventoryService).deleteItemById(1);
        mockMvc.perform(delete("/api/v1/inventory/items/item?id=1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updatesItemsSuccessfully() throws Exception {
        InventoryItemDTO updatedItem = new InventoryItemDTO(1, 20);
        InventoryItemEntity updatedItemEntity = new InventoryItemEntity(1, 20);
        Mockito.doReturn(updatedItemEntity).when(inventoryService).updateItem(any(InventoryItemEntity.class));
        mockMvc.perform(patch("/api/v1/inventory/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatedItem)))
                .andExpect(status().isOk());
    }

    @Test
    void updateItemReturns4xxWhenItemByIdDoesNotExist() throws Exception {
        InventoryItemDTO updatedItem = new InventoryItemDTO(1, 20);
        Mockito.doReturn(null).when(inventoryService).updateItem(any(InventoryItemEntity.class));
        mockMvc.perform(patch("/api/v1/inventory/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatedItem)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItemReturns5xxWhenUpdationFails() throws Exception {
        InventoryItemDTO updatedItem = new InventoryItemDTO(1, 20);
        Mockito.doThrow(RuntimeException.class).when(inventoryService).updateItem(any(InventoryItemEntity.class));
        mockMvc.perform(patch("/api/v1/inventory/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatedItem)))
                .andExpect(status().isInternalServerError());
    }
}
