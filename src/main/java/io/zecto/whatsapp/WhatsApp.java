package io.zecto.whatsapp;

import io.zecto.Transport;

public class WhatsApp {
    public final Messages messages;
    public final Templates templates;
    public final Numbers numbers;
    public final Account account;
    public final Flows flows;

    public WhatsApp(Transport transport) {
        this.messages = new Messages(transport);
        this.templates = new Templates(transport);
        this.numbers = new Numbers(transport);
        this.account = new Account(transport);
        this.flows = new Flows(transport);
    }
}
