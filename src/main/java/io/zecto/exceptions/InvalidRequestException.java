package io.zecto.exceptions;

public class InvalidRequestException extends ZectoException {
    public InvalidRequestException(String detail) {
        super(detail);
    }
}
