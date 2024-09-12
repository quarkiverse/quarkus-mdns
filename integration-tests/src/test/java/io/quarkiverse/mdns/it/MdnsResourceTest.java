package io.quarkiverse.mdns.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWithIgnoringCase;

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
                .body(endsWithIgnoringCase("http://integration:8081/"));
    }
}