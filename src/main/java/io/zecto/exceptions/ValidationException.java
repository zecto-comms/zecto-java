package io.zecto.exceptions;

import java.util.Map;

public class ValidationException extends APIException {
    public ValidationException(int statusCode, String detail, Map<String, Object> body, String requestMethod, String path) {
        super(statusCode, detail, body, requestMethod, path);
    }
}
