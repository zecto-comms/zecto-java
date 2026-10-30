package io.zecto.exceptions;

import java.util.Map;

public class NotFoundException extends APIException {
    public NotFoundException(int statusCode, String detail, Map<String, Object> body, String requestMethod, String path) {
        super(statusCode, detail, body, requestMethod, path);
    }
}
