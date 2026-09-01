package dev.dasha.httpserver;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class SimpleHttpServerIntegrationTest {

    @Test
    void servesStatusEndpointOverHttp() throws Exception {
        SimpleHttpServer server = new SimpleHttpServer(0, 2);
        ExecutorService testExecutor = Executors.newSingleThreadExecutor();

        Future<?> serverTask = testExecutor.submit(() -> {
            try {
                server.start();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });

        try {
            int port = waitForPort(server);

            HttpClient client = HttpClient.newHttpClient();

            java.net.http.HttpRequest request
                    = java.net.http.HttpRequest.newBuilder()
                            .uri(URI.create(
                                    "http://localhost:" + port + "/api/status"
                            ))
                            .GET()
                            .build();

            java.net.http.HttpResponse<String> response
                    = client.send(
                            request,
                            java.net.http.HttpResponse.BodyHandlers.ofString()
                    );

            assertEquals(200, response.statusCode());
            assertEquals("{\"status\":\"ok\"}", response.body());

        } finally {
            server.close();

            serverTask.get(5, TimeUnit.SECONDS);
            testExecutor.shutdownNow();
        }
    }

    private int waitForPort(SimpleHttpServer server)
            throws InterruptedException {

        for (int attempt = 0; attempt < 100; attempt++) {
            try {
                return server.getPort();
            } catch (IllegalStateException exception) {
                Thread.sleep(10);
            }
        }

        throw new IllegalStateException("Server did not start in time");
    }
}
