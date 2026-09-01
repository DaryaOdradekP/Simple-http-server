package dev.dasha.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttpServer implements AutoCloseable {

    private final int requestedPort;
    private final ExecutorService executor;
    private final Router router;

    private volatile boolean running;
    private volatile ServerSocket serverSocket;

    public SimpleHttpServer(int port, int workerCount) {
        this.requestedPort = port;
        this.executor = Executors.newFixedThreadPool(workerCount);
        this.router = new Router();
    }

    public static void main(String[] args) throws IOException {
        try (SimpleHttpServer server = new SimpleHttpServer(8080, 4)) {
            server.start();
        }
    }

    public void start() throws IOException {
        try (ServerSocket socket = new ServerSocket(requestedPort)) {
            serverSocket = socket;
            running = true;

            System.out.println(
                    "Server started: http://localhost:" + getPort()
            );

            while (running) {
                try {
                    Socket clientSocket = socket.accept();

                    executor.submit(
                            () -> handleClient(clientSocket, router)
                    );
                } catch (SocketException exception) {
                    if (running) {
                        throw exception;
                    }
                }
            }
        } finally {
            running = false;
            serverSocket = null;
            executor.shutdown();
        }
    }

    public int getPort() {
        ServerSocket socket = serverSocket;

        if (socket == null) {
            throw new IllegalStateException("Server has not started yet");
        }

        return socket.getLocalPort();
    }

    @Override
    public void close() throws IOException {
        running = false;

        ServerSocket socket = serverSocket;
        if (socket != null) {
            socket.close();
        }

        executor.shutdown();
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
