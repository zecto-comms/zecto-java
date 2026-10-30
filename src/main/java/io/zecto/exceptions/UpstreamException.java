package io.zecto.exceptions;

import java.util.Map;

public class UpstreamException extends APIException {
    public UpstreamException(int statusCode, String detail, Map<String, Object> body, String requestMethod, String path) {
        super(statusCode, detail, body, requestMethod, path);
    }
}
