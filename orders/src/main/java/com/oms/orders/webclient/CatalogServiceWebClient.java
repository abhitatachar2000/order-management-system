package com.oms.orders.webclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.catalog.dto.CatalogDTO;
import com.oms.orders.dto.OrderDTO;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CatalogServiceWebClient {
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    private ObjectMapper om = new ObjectMapper();

    private WebClient webClient;

    public CatalogServiceWebClient (@Qualifier("catalogServiceForOrders") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<CatalogDTO> getCatalogItem(int itemId) {
        String correlationId = MDC.get(CORRELATION_ID_HEADER);
        String uri = String.format("api/v1/catalog/items/%s", itemId);
        return webClient.get()
                .uri(uri)
                .header(CORRELATION_ID_HEADER, correlationId)
                .retrieve()
                .toEntity(CatalogDTO.class)
                .flatMap(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        return Mono.just(response.getBody());
                    } else {
                        return Mono.error(new RuntimeException(String.format("Error fetching the item from catalog. Following error occurred: Status code: %s and Message: %s", response.getStatusCode(), response.getBody())));
                    }
                });
    }
}
