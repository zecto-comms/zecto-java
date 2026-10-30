package io.zecto;

import io.zecto.exceptions.InvalidRequestException;
import io.zecto.whatsapp.WhatsApp;

public class ZectoClient {
    private final Transport transport;
    public final WhatsApp whatsapp;

    public ZectoClient(String apiKey) {
        this(apiKey, Transport.DEFAULT_TIMEOUT);
    }

    public ZectoClient(String apiKey, double timeout) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new InvalidRequestException("apiKey is required");
        }
        this.transport = new Transport(apiKey, timeout);
        this.whatsapp = new WhatsApp(this.transport);
    }

    public void close() {
        transport.close();
    }
}
