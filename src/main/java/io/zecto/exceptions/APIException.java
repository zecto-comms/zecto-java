package io.zecto.exceptions;

import java.util.Map;

public class APIException extends ZectoException {
    private final int statusCode;
    private final String detail;
    private final Map<String, Object> body;
    private final String requestMethod;
    private final String path;

    public APIException(int statusCode, String detail, Map<String, Object> body, String requestMethod, String path) {
        super("[" + statusCode + "] " + detail);
        this.statusCode = statusCode;
        this.detail = detail;
        this.body = body;
        this.requestMethod = requestMethod;
        this.path = path;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDetail() {
        return detail;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getPath() {
        return path;
    }
}
