package com.oms.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = "classpath:/application-test.properties")
@SpringBootTest
class CatalogApplicationTests {

	@Test
	void contextLoads() {
	}

}
