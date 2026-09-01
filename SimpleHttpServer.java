
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

        } catch (IOException | IllegalArgumentException exception) {
            System.err.println("Failed to process request: " + exception.getMessage());
        }
    }
}
