package com.oms.catalog.webClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.inventory.entity.InventoryItemEntity;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class InventoryServiceWebClient {

    private WebClient webClient;

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    private ObjectMapper om = new ObjectMapper();

    public InventoryServiceWebClient(@Qualifier("inventoryService") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Void> createInventoryEntry(InventoryItemEntity inventoryItemEntity) throws JsonProcessingException {
        String correlationId = MDC.get(CORRELATION_ID_HEADER);

        return webClient.post()
                .uri("/api/v1/inventory/items")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(CORRELATION_ID_HEADER, correlationId)
                .bodyValue(om.writeValueAsString(inventoryItemEntity))
                .retrieve()
                .toBodilessEntity()
                .flatMap(resp -> {
                    if (resp.getStatusCode().is2xxSuccessful()) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new RuntimeException("Unexpected response status: " + resp.getStatusCode()));
                    }
                });
    }

    public Mono<InventoryItemEntity> getInventoryItemById(int id) {
        String uri = String.format("/api/v1/inventory/items/%s", id);
        return webClient.get()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve().bodyToMono(InventoryItemEntity.class);
    }
}
