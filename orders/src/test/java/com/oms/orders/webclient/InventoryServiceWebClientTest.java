package com.oms.orders.webclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oms.inventory.dto.InventoryItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

    private InventoryItemDTO testItem;

    @BeforeEach
    void setup() {
        testItem = new InventoryItemDTO(1, 10);
        MDC.put("X-Correlation-ID", "test-correlation-id");
    }

    @Test
    void getsInventoryItemById() {
        ResponseEntity<InventoryItemDTO> responseEntity = new ResponseEntity<>(testItem, HttpStatus.OK);
        Mockito.doReturn(requestBodyUriSpec).when(webClient).get();
        Mockito.doReturn(requestBodySpec).when(requestBodyUriSpec).uri("/api/v1/inventory/items/1");
        Mockito.doReturn(requestBodySpec).when(requestBodySpec).header(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(responseSpec).when(requestBodySpec).retrieve();
        Mockito.doReturn(Mono.just(responseEntity)).when(responseSpec).toEntity(InventoryItemDTO.class);
        StepVerifier.create(inventoryServiceWebClient.getInventoryItemById(1))
                .expectNextMatches(item -> item.getId() == 1 && item.getQuantity() == 10)
                .verifyComplete();
    }

    @Test
    void failsToGetInventoryItemById() {
        ResponseEntity<InventoryItemDTO> responseEntity = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        Mockito.doReturn(requestBodyUriSpec).when(webClient).get();
        Mockito.doReturn(requestBodySpec).when(requestBodyUriSpec).uri("/api/v1/inventory/items/1");
        Mockito.doReturn(requestBodySpec).when(requestBodySpec).header(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(responseSpec).when(requestBodySpec).retrieve();
        Mockito.doReturn(Mono.just(responseEntity)).when(responseSpec).toEntity(InventoryItemDTO.class);
        StepVerifier.create(inventoryServiceWebClient.getInventoryItemById(1)).expectError();
    }

    @Test
    void updatesInventoryAfterOperation() throws JsonProcessingException {
        Mockito.doReturn(requestBodyUriSpec).when(webClient).patch();
        Mockito.doReturn(requestBodySpec).when(requestBodyUriSpec).uri("/api/v1/inventory/items");
        Mockito.doReturn(requestBodySpec).when(requestBodySpec).header(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(Mockito.anyString());
        Mockito.doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        Mockito.doReturn(Mono.just(ResponseEntity.ok().build())).when(responseSpec).toBodilessEntity();
        StepVerifier.create(inventoryServiceWebClient.updateInventoryAfterOperation(testItem)).verifyComplete();
    }

    @Test
    void failsToUpdateInventoryAfterOperation() throws JsonProcessingException {
        Mockito.doReturn(requestBodyUriSpec).when(webClient).patch();
        Mockito.doReturn(requestBodySpec).when(requestBodyUriSpec).uri("/api/v1/inventory/items");
        Mockito.doReturn(requestBodySpec).when(requestBodySpec).header(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(Mockito.anyString());
        Mockito.doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        Mockito.doReturn(Mono.just(ResponseEntity.internalServerError().build())).when(responseSpec).toBodilessEntity();
        StepVerifier.create(inventoryServiceWebClient.updateInventoryAfterOperation(testItem)).expectError();
    }

}
