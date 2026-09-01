package dev.dasha.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class HttpParserTest {

    @Test
    void parsesRequestLineAndHeaders() throws IOException {
        String rawRequest = """
                GET /hello HTTP/1.1\r
                Host: localhost:8080\r
                User-Agent: Test Client\r
                \r
                """;

        BufferedReader input = new BufferedReader(
                new StringReader(rawRequest)
        );

        HttpParser parser = new HttpParser();
        HttpRequest request = parser.parse(input);

        assertEquals("GET", request.getMethod());
        assertEquals("/hello", request.getPath());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("localhost:8080", request.getHeader("Host"));
        assertEquals("localhost:8080", request.getHeader("host"));
        assertEquals("Test Client", request.getHeader("User-Agent"));
    }

    @Test
    void rejectsRequestWithInvalidStartLine() {
        BufferedReader input = new BufferedReader(
                new StringReader("BROKEN\r\n\r\n")
        );

        HttpParser parser = new HttpParser();

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(input)
        );
    }

    @Test
    void rejectsRequestWithoutHeaderTerminator() {
        BufferedReader input = new BufferedReader(
                new StringReader(
                        "GET / HTTP/1.1\r\nHost: localhost:8080\r\n"
                )
        );

        HttpParser parser = new HttpParser();

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(input)
        );
    }
}
