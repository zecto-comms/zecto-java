package io.zecto;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zecto.exceptions.APIConnectionException;
import io.zecto.exceptions.ErrorMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class Transport {
    public static final String BASE_URL = "https://api.zecto.io/whatsapp";
    public static final double DEFAULT_TIMEOUT = 30.0;

    private final HttpClient client;
    private final String apiKey;
    private final Duration timeout;
    private final ObjectMapper mapper = new ObjectMapper();

    public Transport(String apiKey, double timeout) {
        this.apiKey = apiKey;
        this.timeout = Duration.ofMillis((long) (timeout * 1000));
        this.client = HttpClient.newBuilder()
                .connectTimeout(this.timeout)
                .build();
    }

    public Object request(String method, String path, Map<String, Object> params, Object json) {
        String url = BASE_URL + path + buildQuery(params);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .header("X-API-Key", apiKey)
                .header("Content-Type", "application/json");

        HttpRequest.BodyPublisher bodyPublisher;
        if (json != null) {
            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(json);
            } catch (IOException e) {
                throw new APIConnectionException("Failed to serialize request body: " + e.getMessage());
            }
            bodyPublisher = HttpRequest.BodyPublishers.ofString(jsonString);
        } else {
            bodyPublisher = HttpRequest.BodyPublishers.noBody();
        }

        builder.method(method, bodyPublisher);

        HttpResponse<String> response;
        try {
            response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new APIConnectionException(e.getMessage());
        }

        return handleResponse(response, method, path);
    }

    @SuppressWarnings("unchecked")
    private Object handleResponse(HttpResponse<String> response, String method, String path) {
        int status = response.statusCode();
        String text = response.body();

        Map<String, Object> body = Map.of();
        if (text != null && !text.isEmpty()) {
            try {
                Object parsed = mapper.readValue(text, Object.class);
                if (parsed instanceof Map) {
                    body = (Map<String, Object>) parsed;
                }
            } catch (IOException ignored) {
            }
        }

        if (status >= 200 && status < 300) {
            if (text == null || text.isEmpty()) {
                return Map.of();
            }
            try {
                return mapper.readValue(text, Object.class);
            } catch (IOException e) {
                return Map.of();
            }
        }

        Object rawDetail = body.getOrDefault("detail", body.getOrDefault("error",
                (text != null && !text.isEmpty()) ? text : "Unknown error"));
        String detail = formatDetail(rawDetail);
        throw ErrorMapper.forStatus(status, detail, body, method, path);
    }

    @SuppressWarnings("unchecked")
    private String formatDetail(Object detail) {
        if (detail instanceof String s) {
            return s;
        }
        if (detail instanceof List<?> list) {
            List<String> parts = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof Map) {
                    Map<String, Object> m = (Map<String, Object>) item;
                    Object loc = m.get("loc");
                    String field = "";
                    if (loc instanceof List<?> locList) {
                        StringJoiner sj = new StringJoiner(".");
                        for (int i = 0; i < locList.size(); i++) {
                            // drop only the leading "body" segment, not every one
                            if (i == 0 && "body".equals(locList.get(i))) {
                                continue;
                            }
                            sj.add(String.valueOf(locList.get(i)));
                        }
                        field = sj.toString();
                    }
                    String msg = m.containsKey("msg") ? String.valueOf(m.get("msg")) : "invalid";
                    parts.add(field.isEmpty() ? msg : field + ": " + msg);
                } else {
                    parts.add(String.valueOf(item));
                }
            }
            return String.join("; ", parts);
        }
        return String.valueOf(detail);
    }

    /**
     * Percent-encodes a single path segment so interpolated IDs cannot break
     * the URI (spaces, etc.) or traverse the path (encodes '/'). Unreserved
     * characters used by real IDs (letters, digits, '_', '-', '.') are left
     * unchanged. URLEncoder targets query strings, so '+' is remapped to %20.
     */
    public static String encodePathSegment(String segment) {
        return URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String buildQuery(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
                String value = URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8);
                sj.add(key + "=" + value);
            }
        }
        String query = sj.toString();
        return query.isEmpty() ? "" : "?" + query;
    }

    public void close() {
    }
}
