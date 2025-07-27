package com.oms.catalog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.catalog.dto.CatalogDTO;
import com.oms.catalog.entity.CatalogItemEntity;
import com.oms.catalog.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CatalogController.class)
public class CatalogControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogService catalogService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetAllItems() throws Exception {
        CatalogItemEntity itemEntityOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1"
        );
        CatalogItemEntity itemEntityTwo = new CatalogItemEntity(
                "item2",
                13.20,
                "category1"
        );
        Mockito.doReturn(Arrays.asList(itemEntityOne, itemEntityTwo)).when(catalogService).getAllCatalogItems();

        mockMvc.perform(get("/api/v1/catalog/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("item1"))
                .andExpect(jsonPath("$[1].name").value("item2"));
    }

    @Test
    void shouldThrowServerSideErrorIfFetchAllFails() throws Exception {
        Mockito.doThrow(RuntimeException.class).when(catalogService).getAllCatalogItems();
        mockMvc.perform(get("/api/v1/catalog/items")).andExpect(status().is5xxServerError());
    }


    @Test
    void shouldCreateNewItemsSuccessfully() throws Exception {
        CatalogItemEntity itemEntityOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1"
        );
        CatalogDTO itemDTO = new CatalogDTO(
                "item1",
                12.20,
                "category1"
        );
        Mockito.doReturn(itemEntityOne).when(catalogService).addNewItem(any(CatalogItemEntity.class));
        String jsonString = objectMapper.writeValueAsString(itemDTO);
        mockMvc.perform(post("/api/v1/catalog/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldThrow5xxErrorIfCreatingNewItemsFails() throws Exception {
        CatalogDTO itemDTO = new CatalogDTO(
                "item1",
                12.20,
                "category1"
        );
        String jsonString = objectMapper.writeValueAsString(itemDTO);
        Mockito.doThrow(RuntimeException.class).when(catalogService).addNewItem(any(CatalogItemEntity.class));
        mockMvc.perform(post("/api/v1/catalog/items")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldGetOneItem() throws Exception {
        CatalogItemEntity itemEntityOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1"
        );
        Mockito.doReturn(itemEntityOne).when(catalogService).getItemById(1);
        mockMvc.perform(get("/api/v1/catalog/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("item1"));
    }

    @Test
    void shouldReturn404IfItemNotFound() throws Exception {
        CatalogItemEntity itemEntityOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1"
        );
        Mockito.doReturn(null).when(catalogService).getItemById(1);
        mockMvc.perform(get("/api/v1/catalog/items/1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldThrow5xxErrorIfExceptionOccursWhileFindingTheElement() throws Exception {
        CatalogItemEntity itemEntityOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1"
        );
        Mockito.doThrow(RuntimeException.class).when(catalogService).getItemById(1);
        mockMvc.perform(get("/api/v1/catalog/items/1"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldReturnAllItemsOfRequiredCategory() throws Exception {
        CatalogItemEntity itemEntityOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1"
        );
        CatalogItemEntity itemEntityTwo = new CatalogItemEntity(
                "item2",
                14.20,
                "category1"
        );

        Mockito.doReturn(Arrays.asList(itemEntityOne, itemEntityTwo))
                .when(catalogService).getItemsByCategory("category1");
        mockMvc.perform(get("/api/v1/catalog/items/search?category=category1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("item1"))
                .andExpect(jsonPath("$[1].name").value("item2"));
    }

    @Test
    void shouldThrow5xxErrorIfFetchingByCategpryFails() throws Exception {
        Mockito.doThrow(RuntimeException.class)
                .when(catalogService).getItemsByCategory("category1");
        mockMvc.perform(get("/api/v1/catalog/items/search?category=category1"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldUpdateItemsWithSpecifiedId() throws Exception {
        CatalogItemEntity itemEntityOne = new CatalogItemEntity(
                "item1",
                12.20,
                "category1"
        );
        itemEntityOne.setID(1);
        CatalogDTO itemDTO = new CatalogDTO(
                "item1",
                12.20,
                "category1"
        );
        Mockito.doReturn(itemEntityOne).when(catalogService).updateItem(any(Integer.class), any(CatalogItemEntity.class));
        mockMvc.perform(put("/api/v1/catalog/items/1")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldThrow5xxErrorWhenUpdatingItemFails() throws Exception {
        CatalogDTO itemDTO = new CatalogDTO(
                "item1",
                12.20,
                "category1"
        );
        Mockito.doThrow(RuntimeException.class).when(catalogService).updateItem(any(Integer.class), any(CatalogItemEntity.class));
        mockMvc.perform(put("/api/v1/catalog/items/1")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().is5xxServerError());
    }


    @Test
    void shouldDeleteItemForProvidedId() throws Exception {
        Mockito.doReturn(true).when(catalogService).deleteById(1);
        mockMvc.perform(delete("/api/v1/catalog/items/1")).andExpect(status().isNoContent());
    }

    @Test
    void shouldThrow404IfItemNotFoundForDeletion() throws Exception {
        Mockito.doReturn(false).when(catalogService).deleteById(1);
        mockMvc.perform(delete("/api/v1/catalog/items/1")).andExpect(status().is4xxClientError());
    }

    @Test
    void shouldthrow5xxIfItemDeletionFailsBecauseOfException() throws Exception {
        Mockito.doThrow(RuntimeException.class).when(catalogService).deleteById(1);
        mockMvc.perform(delete("/api/v1/catalog/items/1")).andExpect(status().is5xxServerError());
    }

    @Test
    void shouldDeleteAllItemsOfTheProvidedCategory() throws Exception {
        Mockito.doReturn(true).when(catalogService).deleteAllItemsOfCategory("category1");
        mockMvc.perform(delete("/api/v1/catalog/items?category=category1")).andExpect(status().isNoContent());
    }

    @Test
    void shouldThrow404IfNoItemsOfProvidedCategoryExists() throws Exception {
        Mockito.doReturn(false).when(catalogService).deleteAllItemsOfCategory("category1");
        mockMvc.perform(delete("/api/v1/catalog/items?category=category1")).andExpect(status().is4xxClientError());
    }

    @Test
    void shouldThrow5xxStatusCodeIfDeletionByCategoryFailsBecauseOfException() throws Exception {
        Mockito.doThrow(RuntimeException.class).when(catalogService).deleteAllItemsOfCategory("category1");
        mockMvc.perform(delete("/api/v1/catalog/items?category=category1")).andExpect(status().is5xxServerError());
    }
}
