package com.oms.orders.webclient;

import com.oms.catalog.dto.CatalogDTO;
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
public class CatalogServiceWebClientTest {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

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
    private CatalogServiceWebClient catalogServiceWebClient;

    private CatalogDTO catalogDTO;

    @BeforeEach
    void setup() {
        MDC.put(CORRELATION_ID_HEADER, "test-correlation-id");
        catalogDTO = new CatalogDTO("testItem", 10d, "category1");
        catalogDTO.setId(1);
    }

    @Test
    void sucessfullyFetchesTheCatalogItem() {
        ResponseEntity<CatalogDTO> response = new ResponseEntity<CatalogDTO>(catalogDTO, HttpStatus.OK);
        Mockito.doReturn(requestBodyUriSpec).when(webClient).get();
        Mockito.doReturn(requestBodySpec).when(requestBodyUriSpec).uri("api/v1/catalog/items/1");
        Mockito.doReturn(requestBodySpec).when(requestBodySpec).header(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(responseSpec).when(requestBodySpec).retrieve();
        Mockito.doReturn(Mono.just(response)).when(responseSpec).toEntity(CatalogDTO.class);
        StepVerifier.create(catalogServiceWebClient.getCatalogItem(1))
                .expectNextMatches(item -> item.getId() == 1 && item.getCategory() == "category1")
                .verifyComplete();

    }
}
