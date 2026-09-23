package com.kutpenco.datadogloadtest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class StatusResponderController {
    private static final Random RANDOM = new Random();
    private static final List<Integer> COMMON_STATUS_CODES = List.of(
            200, 201, 202, 204, 301, 302, 304, 400, 401, 403, 404, 405, 408, 409, 429, 500, 502, 503, 504);

    @RequestMapping(value = "/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> handleAll(HttpServletRequest request) {
        int status = resolveStatus(request);
        var body = Map.<String, Object>of(
                "timestamp", Instant.now().toString(),
                "request_id", UUID.randomUUID().toString(),
                "method", request.getMethod(),
                "path", request.getRequestURI(),
                "query", request.getQueryString() == null ? "" : request.getQueryString(),
                "status", status,
                "service", "datadog-http-status-load-test",
                "message", "automatic load-test response");

        var builder = ResponseEntity.status(status);
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            builder.header("Allow", "GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS");
        }
        if ("HEAD".equalsIgnoreCase(request.getMethod()) || status == 204) {
            return builder.build();
        }
        return builder.body(body);
    }

    private int resolveStatus(HttpServletRequest request) {
        String requested = request.getParameter("status");
        if ("random".equalsIgnoreCase(requested) || request.getRequestURI().contains("/random")) {
            return COMMON_STATUS_CODES.get(RANDOM.nextInt(COMMON_STATUS_CODES.size()));
        }
        if (requested != null) {
            try {
                int status = Integer.parseInt(requested);
                if (status >= 100 && status <= 599) return status;
            } catch (NumberFormatException ignored) {
                // Use the method default for an invalid status parameter.
            }
        }
        return switch (request.getMethod().toUpperCase()) {
            case "POST" -> 201;
            case "PUT" -> 202;
            case "DELETE", "OPTIONS" -> 204;
            default -> 200;
        };
    }
}
