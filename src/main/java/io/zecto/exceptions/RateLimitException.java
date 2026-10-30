package io.zecto.exceptions;

import java.util.Map;

public class RateLimitException extends APIException {
    public RateLimitException(int statusCode, String detail, Map<String, Object> body, String requestMethod, String path) {
        super(statusCode, detail, body, requestMethod, path);
    }
}
