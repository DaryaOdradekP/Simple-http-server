package dev.dasha.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttpServer {

    private static final int PORT = 8080;
    private static final int WORKER_COUNT = 4;

    public static void main(String[] args) throws IOException {
        Router router = new Router();

        try (ServerSocket serverSocket = new ServerSocket(PORT); ExecutorService executor = Executors.newFixedThreadPool(WORKER_COUNT)) {
            System.out.println("Server started: http://localhost:" + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                executor.submit(() -> handleClient(clientSocket, router));
            }
        }
    }

    private static void handleClient(Socket clientSocket, Router router) {
        try (
                Socket socket = clientSocket; BufferedReader input = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                ); OutputStream output = socket.getOutputStream()) {
            try {
                HttpParser parser = new HttpParser();
                HttpRequest request = parser.parse(input);

                System.out.printf(
                        "[%s] %s %s%n",
                        Thread.currentThread().getName(),
                        request.getMethod(),
                        request.getPath()
                );

                HttpResponse response = router.route(request);
                output.write(response.toBytes());

            } catch (IllegalArgumentException exception) {
                System.err.println("Bad request: " + exception.getMessage());

                HttpResponse response = new HttpResponse(
                        400,
                        "Bad Request",
                        "text/html; charset=UTF-8",
                        "<h1>400 Bad Request</h1><p>The server could not parse this request.</p>"
                );

                output.write(response.toBytes());

            } catch (IOException exception) {
                System.err.println("Server error: " + exception.getMessage());

                HttpResponse response = new HttpResponse(
                        500,
                        "Internal Server Error",
                        "text/html; charset=UTF-8",
                        "<h1>500 Internal Server Error</h1><p>Something went wrong on the server.</p>"
                );

                output.write(response.toBytes());
            }

        } catch (IOException exception) {
            System.err.println("Could not send response: " + exception.getMessage());
        }
    }
}
