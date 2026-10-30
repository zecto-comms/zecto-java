package io.zecto.exceptions;

import java.util.Map;

public class PermissionDeniedException extends APIException {
    public PermissionDeniedException(int statusCode, String detail, Map<String, Object> body, String requestMethod, String path) {
        super(statusCode, detail, body, requestMethod, path);
    }
}
