package io.zecto.exceptions;

import java.util.Map;

public class ServerException extends APIException {
    public ServerException(int statusCode, String detail, Map<String, Object> body, String requestMethod, String path) {
        super(statusCode, detail, body, requestMethod, path);
    }
}
