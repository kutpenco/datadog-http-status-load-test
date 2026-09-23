package com.kutpenco.datadogloadtest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final int MAX_BODY_LENGTH = 2000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var wrappedRequest = new ContentCachingRequestWrapper(request);
        var wrappedResponse = new ContentCachingResponseWrapper(response);
        long started = System.nanoTime();
        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long elapsedMs = (System.nanoTime() - started) / 1_000_000L;
            log.info("DATADOG_LOAD_TEST request_ts={} method={} uri={} query={} status={} elapsed_ms={} "
                            + "remote_addr={} user_agent={} request_body={} response_body={}",
                    Instant.now(), request.getMethod(), request.getRequestURI(), request.getQueryString(),
                    wrappedResponse.getStatus(), elapsedMs, request.getRemoteAddr(),
                    request.getHeader("User-Agent"), body(wrappedRequest.getContentAsByteArray()),
                    body(wrappedResponse.getContentAsByteArray()));
            wrappedResponse.copyBodyToResponse();
        }
    }

    private String body(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";
        String value = new String(bytes, StandardCharsets.UTF_8);
        return value.length() > MAX_BODY_LENGTH ? value.substring(0, MAX_BODY_LENGTH) + "..." : value;
    }
}
