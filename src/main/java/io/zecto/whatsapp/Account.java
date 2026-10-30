package io.zecto.whatsapp;

import io.zecto.Transport;

import java.util.Map;

public class Account {
    private final Transport transport;

    public Account(Transport transport) {
        this.transport = transport;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> get() {
        return (Map<String, Object>) transport.request("GET", "/v1/account", null, null);
    }
}
