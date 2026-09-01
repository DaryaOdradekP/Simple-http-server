package dev.dasha.httpserver;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;

    public HttpRequest(
            String method,
            String path,
            String version,
            Map<String, String> headers
    ) {
        this.method = method;
        this.path = path;
        this.version = version;

        Map<String, String> normalizedHeaders = new LinkedHashMap<>();

        headers.forEach((name, value)
                -> normalizedHeaders.put(name.toLowerCase(Locale.ROOT), value)
        );

        this.headers = Collections.unmodifiableMap(normalizedHeaders);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase(Locale.ROOT));
    }
}
