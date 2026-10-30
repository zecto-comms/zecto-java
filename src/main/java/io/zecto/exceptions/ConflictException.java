package io.zecto.exceptions;

import java.util.Map;

public class ConflictException extends APIException {
    public ConflictException(int statusCode, String detail, Map<String, Object> body, String requestMethod, String path) {
        super(statusCode, detail, body, requestMethod, path);
    }
}
