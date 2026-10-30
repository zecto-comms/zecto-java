package io.zecto.whatsapp;

import io.zecto.Transport;
import io.zecto.exceptions.InvalidRequestException;

import java.util.LinkedHashMap;
import java.util.Map;

public class Flows {
    private final Transport transport;

    public Flows(Transport transport) {
        this.transport = transport;
    }

    public Object list() {
        return list(null);
    }

    public Object list(String status) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("status", status);
        return transport.request("GET", "/v1/flows", params, null);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> rename(String flowId, String name) {
        if (flowId == null || flowId.isEmpty()) {
            throw new InvalidRequestException("flowId is required");
        }
        if (name == null || name.isEmpty()) {
            throw new InvalidRequestException("name is required");
        }
        return (Map<String, Object>) transport.request("POST", "/v1/flows/" + Transport.encodePathSegment(flowId) + "/rename", null, Map.of("name", name));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> setStatus(String flowId, String status) {
        if (flowId == null || flowId.isEmpty()) {
            throw new InvalidRequestException("flowId is required");
        }
        if (status == null || status.isEmpty()) {
            throw new InvalidRequestException("status is required");
        }
        return (Map<String, Object>) transport.request("POST", "/v1/flows/" + Transport.encodePathSegment(flowId) + "/status", null, Map.of("status", status));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> trigger(String flowId, String to) {
        if (flowId == null || flowId.isEmpty()) {
            throw new InvalidRequestException("flowId is required");
        }
        if (to == null || to.isEmpty()) {
            throw new InvalidRequestException("to is required");
        }
        return (Map<String, Object>) transport.request("POST", "/v1/flows/" + Transport.encodePathSegment(flowId) + "/trigger", null, Map.of("to", to));
    }
}
