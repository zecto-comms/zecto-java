package io.zecto.whatsapp;

import io.zecto.Transport;
import io.zecto.exceptions.InvalidRequestException;

import java.util.Map;

public class Numbers {
    private final Transport transport;

    public Numbers(Transport transport) {
        this.transport = transport;
    }

    public Object list() {
        return transport.request("GET", "/v1/numbers", null, null);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> get(String phoneNumberId) {
        if (phoneNumberId == null || phoneNumberId.isEmpty()) {
            throw new InvalidRequestException("phoneNumberId is required");
        }
        return (Map<String, Object>) transport.request("GET", "/v1/numbers/" + Transport.encodePathSegment(phoneNumberId), null, null);
    }
}
