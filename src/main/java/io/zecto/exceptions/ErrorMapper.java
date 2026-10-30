package io.zecto.exceptions;

import java.util.Map;

public final class ErrorMapper {
    private ErrorMapper() {
    }

    public static APIException forStatus(
            int statusCode,
            String detail,
            Map<String, Object> body,
            String requestMethod,
            String path) {
        return switch (statusCode) {
            case 400, 422 -> new ValidationException(statusCode, detail, body, requestMethod, path);
            case 401 -> new AuthenticationException(statusCode, detail, body, requestMethod, path);
            case 403 -> new PermissionDeniedException(statusCode, detail, body, requestMethod, path);
            case 404 -> new NotFoundException(statusCode, detail, body, requestMethod, path);
            case 409 -> new ConflictException(statusCode, detail, body, requestMethod, path);
            case 429 -> new RateLimitException(statusCode, detail, body, requestMethod, path);
            case 502 -> new UpstreamException(statusCode, detail, body, requestMethod, path);
            default -> statusCode >= 500
                    ? new ServerException(statusCode, detail, body, requestMethod, path)
                    : new APIException(statusCode, detail, body, requestMethod, path);
        };
    }
}
