package com.mb.microservices.inventory;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;


@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {


	@BeforeAll
	static void start() {
		TestcontainersConfiguration.mysqlContainer().start();
	}
	@LocalServerPort
	Integer port;


	@Test
	void testIsInStock() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
		Boolean resp = RestAssured.given().when().get("/api/inventory?skuCode=iphone_15&quantity=100")
				.then().statusCode(200).extract().response().as(Boolean.class);
		Assertions.assertTrue(resp);
		resp = RestAssured.given().when().get("/api/inventory?skuCode=iphone_15&quantity=101")
				.then().statusCode(200).extract().response().as(Boolean.class);
		Assertions.assertFalse(resp);

	}

}
