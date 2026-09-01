package dev.dasha.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Router {

    public HttpResponse route(HttpRequest request) throws IOException {
        String path = request.getPath();

        if (!"GET".equals(request.getMethod())) {
            return new HttpResponse(
                    405,
                    "Method Not Allowed",
                    "text/html; charset=UTF-8",
                    "<h1>405 Method Not Allowed</h1><p>This server currently supports GET requests only.</p>"
            );
        }

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
        String resourcePath = "/public/" + fileName;

        try (InputStream input = Router.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IOException("Static file not found: " + resourcePath);
            }

            String html = new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            return new HttpResponse(
                    200,
                    "OK",
                    "text/html; charset=UTF-8",
                    html
            );
        }
    }
}
