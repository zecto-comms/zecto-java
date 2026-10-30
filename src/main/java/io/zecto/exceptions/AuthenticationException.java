package io.zecto.exceptions;

import java.util.Map;

public class AuthenticationException extends APIException {
    public AuthenticationException(int statusCode, String detail, Map<String, Object> body, String requestMethod, String path) {
        super(statusCode, detail, body, requestMethod, path);
    }
}
