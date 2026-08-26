
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SimpleHttpServer {

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server started: http://localhost:8080");

            try (Socket clientSocket = serverSocket.accept(); BufferedReader input = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8)); OutputStream output = clientSocket.getOutputStream()) {

                HttpParser parser = new HttpParser();
                HttpRequest request = parser.parse(input);

                System.out.println("Parsed request:");
                System.out.println("Method: " + request.getMethod());
                System.out.println("Path: " + request.getPath());
                System.out.println("Version: " + request.getVersion());
                System.out.println("Host: " + request.getHeader("Host"));

                HttpResponse response = new HttpResponse(
                        200,
                        "OK",
                        "text/html; charset=UTF-8",
                        "<h1>Hello from Java!</h1>"
                );

                output.write(response.toBytes());
            }
        }
    }
}
