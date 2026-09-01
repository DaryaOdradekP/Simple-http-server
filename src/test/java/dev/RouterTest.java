package dev.dasha.httpserver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class RouterTest {

    private final Router router = new Router();

    @Test
    void servesHomePageFromResources() throws IOException {
        HttpResponse response = router.route(request("GET", "/"));

        String rawResponse = new String(
                response.toBytes(),
                StandardCharsets.UTF_8
        );

        assertTrue(rawResponse.startsWith("HTTP/1.1 200 OK\r\n"));
        assertTrue(rawResponse.contains("Content-Type: text/html; charset=UTF-8"));
        assertTrue(rawResponse.contains("My Java HTTP Server"));
    }

    @Test
    void returnsJsonForStatusRoute() throws IOException {
        HttpResponse response = router.route(request("GET", "/api/status"));

        String rawResponse = new String(
                response.toBytes(),
                StandardCharsets.UTF_8
        );

        assertTrue(rawResponse.startsWith("HTTP/1.1 200 OK\r\n"));
        assertTrue(rawResponse.contains("Content-Type: application/json; charset=UTF-8"));
        assertTrue(rawResponse.endsWith("{\"status\":\"ok\"}"));
    }

    @Test
    void returns404ForUnknownRoute() throws IOException {
        HttpResponse response = router.route(request("GET", "/missing"));

        String rawResponse = new String(
                response.toBytes(),
                StandardCharsets.UTF_8
        );

        assertTrue(rawResponse.startsWith("HTTP/1.1 404 Not Found\r\n"));
    }

    @Test
    void returns405ForUnsupportedMethod() throws IOException {
        HttpResponse response = router.route(request("POST", "/hello"));

        String rawResponse = new String(
                response.toBytes(),
                StandardCharsets.UTF_8
        );

        assertTrue(rawResponse.startsWith("HTTP/1.1 405 Method Not Allowed\r\n"));
    }

    private HttpRequest request(String method, String path) {
        return new HttpRequest(
                method,
                path,
                "HTTP/1.1",
                Map.of()
        );
    }
}
