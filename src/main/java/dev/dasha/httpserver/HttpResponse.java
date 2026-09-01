package dev.dasha.httpserver;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private final int statusCode;
    private final String reasonPhrase;
    private final String contentType;
    private final String body;

    public HttpResponse(
            int statusCode,
            String reasonPhrase,
            String contentType,
            String body
    ) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.contentType = contentType;
        this.body = body;
    }

    public byte[] toBytes() {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        String rawResponse = """
                HTTP/1.1 %d %s\r
                Content-Type: %s\r
                Content-Length: %d\r
                Connection: close\r
                \r
                %s""".formatted(
                statusCode,
                reasonPhrase,
                contentType,
                bodyBytes.length,
                body
        );

        return rawResponse.getBytes(StandardCharsets.UTF_8);
    }
}
