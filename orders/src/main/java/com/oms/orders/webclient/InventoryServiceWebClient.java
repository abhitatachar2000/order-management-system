package com.oms.orders.webclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.inventory.dto.InventoryItemDTO;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class InventoryServiceWebClient {
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    private ObjectMapper om = new ObjectMapper();

    private WebClient webClient;

    public InventoryServiceWebClient (@Qualifier("inventoryServiceForOrders") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<InventoryItemDTO> getInventoryItemById(int itemId) {
        String uri = String.format("/api/v1/inventory/items/%s", itemId);
        String correlationID = MDC.get(CORRELATION_ID_HEADER);
        return webClient.get()
                .uri(uri)
                .header(CORRELATION_ID_HEADER, correlationID)
                .retrieve().toEntity(InventoryItemDTO.class)
                .flatMap(resposne -> {
                    if (resposne.getStatusCode().is2xxSuccessful()) {
                        return Mono.just(resposne.getBody());
                    } else {
                        return Mono.error(new RuntimeException(String.format("Error occurred. Response status is %s and message %s", resposne.getStatusCode(), resposne.getBody())));
                    }
                });
    }

    public Mono<Void> updateInventoryAfterOperation(InventoryItemDTO inventoryItemDTO) throws JsonProcessingException {
        String uri = "/api/v1/inventory/items";
        String correlationID = MDC.get(CORRELATION_ID_HEADER);
        return webClient.put()
                .uri(uri)
                .header(CORRELATION_ID_HEADER, correlationID)
                .bodyValue(om.writeValueAsString(inventoryItemDTO))
                .retrieve()
                .toBodilessEntity()
                .flatMap(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new RuntimeException(String.format("Error occurred. Response status is %s and message %s", response.getStatusCode(), response.getBody())));
                    }
                });
    }
}
