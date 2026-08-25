
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

                System.out.println("Request from browser:");

                String line;
                while ((line = input.readLine()) != null && !line.isEmpty()) {
                    System.out.println(line);
                }

                String html = "<h1>Hello from Java!</h1>";
                byte[] body = html.getBytes(StandardCharsets.UTF_8);

                String response = """
                        HTTP/1.1 200 OK\r
                        Content-Type: text/html; charset=UTF-8\r
                        Content-Length: %d\r
                        Connection: close\r
                        \r
                        %s""".formatted(body.length, html);

                output.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
