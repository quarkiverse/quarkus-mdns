package io.quarkiverse.mdns.test;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;

public class MdnsDevModeTest {

    private static Class<?>[] testClasses = {
            JmDNSResource.class
    };

    // Start hot reload (DevMode) test with your extension loaded
    @RegisterExtension
    static final QuarkusDevModeTest test = new QuarkusDevModeTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(testClasses)
                    .addAsResource("application-dev-mode.properties", "application.properties"));

    @Test
    public void testListService() throws Exception {
        RestAssured.given()
                .when().header("Content-Type", "plain/text")
                .post("/jmdns/list")
                .then()
                .statusCode(200)
                .body(is("http://testing.local:8080/"));
    }
}