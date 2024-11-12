package com.mb.microservices.product;

import io.restassured.RestAssured;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor
class ProductServiceApplicationTests {

    @LocalServerPort
    private Integer port;

    @BeforeAll
    static void start() {
        TestcontainersConfiguration.mongoDbContainer().start();
    }

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }


    @Test
    void testCreateProduct() {
        String requestBody = """
                    {
                        "name" : "Iphone 15",
                        "description": "Apple Iphone 15 128 GB",
                        "price": 70000
                    }
                """;
        RestAssured.given().contentType("application/json")
                .body(requestBody).when().post("/api/product")
                .then().statusCode(201)
                .body("id", Matchers.notNullValue())
                .body("name", Matchers.equalTo("Iphone 15"))
                .body("description", Matchers.equalTo("Apple Iphone 15 128 GB"))
                .body("price", Matchers.equalTo(70000));
    }

}
