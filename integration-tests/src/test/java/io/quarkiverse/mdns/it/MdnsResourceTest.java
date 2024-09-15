package io.quarkiverse.mdns.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MdnsResourceTest {

    @Test
    public void testServiceEndpoint() {
        given()
                .when().get("/mdns")
                .then()
                .statusCode(200)
                .body(is("http://mdns-tester.local:8081/"));
    }
}