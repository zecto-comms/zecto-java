package io.zecto.whatsapp;

import io.zecto.Transport;
import io.zecto.Payloads;
import io.zecto.exceptions.InvalidRequestException;

import java.util.List;
import java.util.Map;

public class Messages {
    private final Transport transport;

    public Messages(Transport transport) {
        this.transport = transport;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> send(Map<String, Object> payload) {
        return (Map<String, Object>) transport.request("POST", "/v1/messages/send", null, payload);
    }

    public Map<String, Object> sendText(String phoneNumberId, String to, String body) {
        return sendText(phoneNumberId, to, body, false, null);
    }

    public Map<String, Object> sendText(String phoneNumberId, String to, String body, boolean previewUrl) {
        return sendText(phoneNumberId, to, body, previewUrl, null);
    }

    public Map<String, Object> sendText(String phoneNumberId, String to, String body, boolean previewUrl, String replyTo) {
        return send(Payloads.text(phoneNumberId, to, body, previewUrl, replyTo));
    }

    public Map<String, Object> sendImage(String phoneNumberId, String to, String link) {
        return send(Payloads.image(phoneNumberId, to, link, null, null, null));
    }

    public Map<String, Object> sendImage(String phoneNumberId, String to, String link, String mediaId, String caption, String replyTo) {
        return send(Payloads.image(phoneNumberId, to, link, mediaId, caption, replyTo));
    }

    public Map<String, Object> sendVideo(String phoneNumberId, String to, String link) {
        return send(Payloads.video(phoneNumberId, to, link, null, null, null));
    }

    public Map<String, Object> sendVideo(String phoneNumberId, String to, String link, String mediaId, String caption, String replyTo) {
        return send(Payloads.video(phoneNumberId, to, link, mediaId, caption, replyTo));
    }

    public Map<String, Object> sendAudio(String phoneNumberId, String to, String link) {
        return send(Payloads.audio(phoneNumberId, to, link, null, null));
    }

    public Map<String, Object> sendAudio(String phoneNumberId, String to, String link, String mediaId, String replyTo) {
        return send(Payloads.audio(phoneNumberId, to, link, mediaId, replyTo));
    }

    public Map<String, Object> sendDocument(String phoneNumberId, String to, String link) {
        return send(Payloads.document(phoneNumberId, to, link, null, null, null, null));
    }

    public Map<String, Object> sendDocument(String phoneNumberId, String to, String link, String mediaId, String caption, String filename, String replyTo) {
        return send(Payloads.document(phoneNumberId, to, link, mediaId, caption, filename, replyTo));
    }

    public Map<String, Object> sendLocation(String phoneNumberId, String to, double latitude, double longitude) {
        return send(Payloads.location(phoneNumberId, to, latitude, longitude, null, null, null));
    }

    public Map<String, Object> sendLocation(String phoneNumberId, String to, double latitude, double longitude, String name, String address, String replyTo) {
        return send(Payloads.location(phoneNumberId, to, latitude, longitude, name, address, replyTo));
    }

    public Map<String, Object> sendContacts(String phoneNumberId, String to, List<Map<String, Object>> contacts) {
        return send(Payloads.contacts(phoneNumberId, to, contacts, null));
    }

    public Map<String, Object> sendContacts(String phoneNumberId, String to, List<Map<String, Object>> contacts, String replyTo) {
        return send(Payloads.contacts(phoneNumberId, to, contacts, replyTo));
    }

    public Map<String, Object> sendButtons(String phoneNumberId, String to, String body, List<Object> buttons) {
        return send(Payloads.interactiveButtons(phoneNumberId, to, body, buttons, null, null, null));
    }

    public Map<String, Object> sendButtons(String phoneNumberId, String to, String body, List<Object> buttons, String header, String footer, String replyTo) {
        return send(Payloads.interactiveButtons(phoneNumberId, to, body, buttons, header, footer, replyTo));
    }

    public Map<String, Object> sendList(String phoneNumberId, String to, String body, String button, List<Map<String, Object>> sections) {
        return send(Payloads.interactiveList(phoneNumberId, to, body, button, sections, null, null, null));
    }

    public Map<String, Object> sendList(String phoneNumberId, String to, String body, String button, List<Map<String, Object>> sections, String header, String footer, String replyTo) {
        return send(Payloads.interactiveList(phoneNumberId, to, body, button, sections, header, footer, replyTo));
    }

    public Map<String, Object> sendCtaUrl(String phoneNumberId, String to, String body, String displayText, String url) {
        return send(Payloads.interactiveCtaUrl(phoneNumberId, to, body, displayText, url, null, null, null));
    }

    public Map<String, Object> sendCtaUrl(String phoneNumberId, String to, String body, String displayText, String url, String header, String footer, String replyTo) {
        return send(Payloads.interactiveCtaUrl(phoneNumberId, to, body, displayText, url, header, footer, replyTo));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> sendTemplate(String phoneNumberId, String to, String templateId) {
        return (Map<String, Object>) transport.request("POST", "/v1/messages/send-template", null,
                Payloads.template(phoneNumberId, to, templateId, null, null, null));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> sendTemplate(String phoneNumberId, String to, String templateId, Object body, Object header, Object buttons) {
        return (Map<String, Object>) transport.request("POST", "/v1/messages/send-template", null,
                Payloads.template(phoneNumberId, to, templateId, body, header, buttons));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> markRead(String phoneNumberId, String messageId) {
        return markRead(phoneNumberId, messageId, false);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> markRead(String phoneNumberId, String messageId, boolean typing) {
        return (Map<String, Object>) transport.request("POST", "/v1/messages/read", null,
                Payloads.readReceipt(phoneNumberId, messageId, typing));
    }

    public Object list(Map<String, Object> params) {
        return transport.request("GET", "/v1/messages", params, null);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> get(String messageId) {
        if (messageId == null || messageId.isEmpty()) {
            throw new InvalidRequestException("messageId is required");
        }
        return (Map<String, Object>) transport.request("GET", "/v1/messages/" + Transport.encodePathSegment(messageId), null, null);
    }
}
