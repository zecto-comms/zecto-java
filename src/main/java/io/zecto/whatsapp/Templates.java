package io.zecto.whatsapp;

import io.zecto.Transport;
import io.zecto.exceptions.InvalidRequestException;

import java.util.LinkedHashMap;
import java.util.Map;

public class Templates {
    private final Transport transport;

    public Templates(Transport transport) {
        this.transport = transport;
    }

    public Object list() {
        return list(null, null, null);
    }

    public Object list(String status, String category, String search) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("status", status);
        params.put("category", category);
        params.put("search", search);
        return transport.request("GET", "/v1/templates", params, null);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> exampleConfig(String templateId) {
        if (templateId == null || templateId.isEmpty()) {
            throw new InvalidRequestException("templateId is required");
        }
        return (Map<String, Object>) transport.request("GET", "/v1/templates/example-config/" + Transport.encodePathSegment(templateId), null, null);
    }
}
