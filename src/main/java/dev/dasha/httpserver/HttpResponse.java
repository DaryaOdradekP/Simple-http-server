package dev.dasha.httpserver;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private final int statusCode;
    private final String reasonPhrase;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(
            int statusCode,
            String reasonPhrase,
            String contentType,
            String body
    ) {
        this(statusCode, reasonPhrase, contentType, body, Map.of());
    }

    public HttpResponse(
            int statusCode,
            String reasonPhrase,
            String contentType,
            String body,
            Map<String, String> additionalHeaders
    ) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.body = body.getBytes(StandardCharsets.UTF_8);
        this.headers = new LinkedHashMap<>();

        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(this.body.length));
        headers.put("Connection", "close");
        headers.putAll(additionalHeaders);
    }

    public byte[] toBytes() {
        StringBuilder responseHead = new StringBuilder();

        responseHead.append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(reasonPhrase)
                .append(CRLF);

        headers.forEach((name, value)
                -> responseHead.append(name)
                        .append(": ")
                        .append(value)
                        .append(CRLF)
        );

        responseHead.append(CRLF);

        byte[] headerBytes = responseHead
                .toString()
                .getBytes(StandardCharsets.UTF_8);

        byte[] responseBytes = new byte[headerBytes.length + body.length];

        System.arraycopy(
                headerBytes, 0,
                responseBytes, 0,
                headerBytes.length
        );

        System.arraycopy(
                body, 0,
                responseBytes, headerBytes.length,
                body.length
        );

        return responseBytes;
    }
}
