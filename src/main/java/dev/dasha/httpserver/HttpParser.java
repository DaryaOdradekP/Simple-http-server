package dev.dasha.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpParser {

    public HttpRequest parse(BufferedReader input) throws IOException {
        String requestLine = input.readLine();

        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("Request line is missing");
        }

        String[] requestParts = requestLine.split("\\s+");

        if (requestParts.length != 3) {
            throw new IllegalArgumentException("Invalid request line: " + requestLine);
        }

        String method = requestParts[0];
        String path = requestParts[1];
        String version = requestParts[2];

        Map<String, String> headers = new LinkedHashMap<>();

        while (true) {
            String headerLine = input.readLine();

            if (headerLine == null) {
                throw new IllegalArgumentException(
                        "Request headers are incomplete"
                );
            }

            if (headerLine.isEmpty()) {
                break;
            }

            int colonIndex = headerLine.indexOf(':');

            if (colonIndex <= 0) {
                throw new IllegalArgumentException(
                        "Invalid header: " + headerLine
                );
            }

            String name = headerLine.substring(0, colonIndex).trim();
            String value = headerLine.substring(colonIndex + 1).trim();

            headers.put(name, value);
        }

        return new HttpRequest(method, path, version, headers);
    }
}
