
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Router {

    public HttpResponse route(HttpRequest request) throws IOException {
        String path = request.getPath();

        if (path.equals("/")) {
            return htmlFile("index.html");
        }

        if (path.equals("/hello")) {
            return new HttpResponse(
                    200,
                    "OK",
                    "text/html; charset=UTF-8",
                    "<h1>Hello!</h1><p>This response came from the /hello route.</p>"
            );
        }

        if (path.equals("/about")) {
            return htmlFile("about.html");
        }

        if (path.equals("/api/status")) {
            return new HttpResponse(
                    200,
                    "OK",
                    "application/json; charset=UTF-8",
                    "{\"status\":\"ok\"}"
            );
        }

        return new HttpResponse(
                404,
                "Not Found",
                "text/html; charset=UTF-8",
                "<h1>404 Not Found</h1><p>No route matches this path.</p>"
        );
    }

    private HttpResponse htmlFile(String fileName) throws IOException {
        Path filePath = Path.of("public", fileName);
        String html = Files.readString(filePath, StandardCharsets.UTF_8);

        return new HttpResponse(
                200,
                "OK",
                "text/html; charset=UTF-8",
                html
        );
    }
}
