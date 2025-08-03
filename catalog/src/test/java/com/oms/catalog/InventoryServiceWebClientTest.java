package com.oms.catalog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.catalog.webClient.InventoryServiceWebClient;
import com.oms.inventory.entity.InventoryItemEntity;
import com.oms.inventory.service.InventoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceWebClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private InventoryServiceWebClient inventoryServiceWebClient;

    private InventoryItemEntity testItem;

    @BeforeEach
    void setup() {
       testItem = new InventoryItemEntity(1, 10);
       MDC.put("X-Correlation-ID", "test-correlation-id");
    }

    @Test
    void createsANewInventoryItemSuccessfully() throws JsonProcessingException {

        Mockito.doReturn(requestBodyUriSpec).when(webClient).post();
        Mockito.doReturn(requestBodySpec).when(requestBodyUriSpec).uri("/api/v1/inventory/items");
        Mockito.doReturn(requestBodySpec).when(requestBodySpec).header(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(Mockito.anyString());
        Mockito.doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        Mockito.doReturn(Mono.just(ResponseEntity.ok().build())).when(responseSpec).toBodilessEntity();

        Mono<Void> result = inventoryServiceWebClient.createInventoryEntry(testItem);

        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void creatingNewInventoryItemFails() throws JsonProcessingException {

        Mockito.doReturn(requestBodyUriSpec).when(webClient).post();
        Mockito.doReturn(requestBodySpec).when(requestBodyUriSpec).uri("/api/v1/inventory/items");
        Mockito.doReturn(requestBodySpec).when(requestBodySpec).header(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(Mockito.anyString());
        Mockito.doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        Mockito.doReturn(Mono.just(ResponseEntity.internalServerError().build())).when(responseSpec).toBodilessEntity();

        Mono<Void> result = inventoryServiceWebClient.createInventoryEntry(testItem);

        StepVerifier.create(result).expectError();
    }

    @Test
    void getInventoryItemById() {
        ResponseEntity<InventoryItemEntity> responseEntity = new ResponseEntity<>(testItem, HttpStatus.OK);
        Mockito.doReturn(requestBodyUriSpec).when(webClient).get();
        Mockito.doReturn(requestBodySpec).when(requestBodyUriSpec).uri("/api/v1/inventory/items/1");
        Mockito.doReturn(requestBodySpec).when(requestBodySpec).header(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(responseSpec).when(requestBodySpec).retrieve();
        Mockito.doReturn(Mono.just(responseEntity)).when(responseSpec).toEntity(InventoryItemEntity.class);

        StepVerifier.create(inventoryServiceWebClient.getInventoryItemById(1))
                .expectNextMatches(item -> item.getId() == 1 && item.getQuantity() == 10)
                .verifyComplete();
    }

    @Test
    void getInventoryItemByIdFails() {
        ResponseEntity<InventoryItemEntity> responseEntity = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        Mockito.doReturn(requestBodyUriSpec).when(webClient).get();
        Mockito.doReturn(requestBodySpec).when(requestBodyUriSpec).uri("/api/v1/inventory/items/1");
        Mockito.doReturn(requestBodySpec).when(requestBodySpec).header(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(responseSpec).when(requestBodySpec).retrieve();
        Mockito.doReturn(Mono.just(responseEntity)).when(responseSpec).toEntity(InventoryItemEntity.class);

        StepVerifier.create(inventoryServiceWebClient.getInventoryItemById(1)).expectError();
    }
}
