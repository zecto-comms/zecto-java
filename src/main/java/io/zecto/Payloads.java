package io.zecto;

import io.zecto.exceptions.InvalidRequestException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Payloads {
    private Payloads() {
    }

    private static String digitsOnly(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> fillWaIds(List<Map<String, Object>> contactsList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> contact : contactsList) {
            Map<String, Object> newContact = new LinkedHashMap<>(contact);
            Object phones = newContact.get("phones");
            if (phones instanceof List<?> phoneList) {
                List<Map<String, Object>> newPhones = new ArrayList<>();
                for (Object p : phoneList) {
                    if (p instanceof Map) {
                        Map<String, Object> phone = new LinkedHashMap<>((Map<String, Object>) p);
                        Object waId = phone.get("wa_id");
                        Object phoneNum = phone.get("phone");
                        if ((waId == null || "".equals(waId)) && phoneNum != null) {
                            phone.put("wa_id", digitsOnly(String.valueOf(phoneNum)));
                        }
                        newPhones.add(phone);
                    }
                }
                newContact.put("phones", newPhones);
            }
            result.add(newContact);
        }
        return result;
    }

    private static Map<String, Object> base(String phoneNumberId, String to, String msgType, String replyTo) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("phone_number_id", phoneNumberId);
        payload.put("to", to);
        payload.put("type", msgType);
        if (replyTo != null && !replyTo.isEmpty()) {
            payload.put("context", Map.of("message_id", replyTo));
        }
        return payload;
    }

    private static Map<String, Object> mediaObject(String link, String mediaId, String caption, String filename) {
        boolean noLink = link == null || link.isEmpty();
        boolean noMedia = mediaId == null || mediaId.isEmpty();
        if (noLink && noMedia) {
            throw new InvalidRequestException("Provide either 'link' or 'mediaId'");
        }
        Map<String, Object> obj = new LinkedHashMap<>();
        if (!noLink) {
            obj.put("link", link);
        }
        if (!noMedia) {
            obj.put("id", mediaId);
        }
        if (caption != null) {
            obj.put("caption", caption);
        }
        if (filename != null) {
            obj.put("filename", filename);
        }
        return obj;
    }

    public static Map<String, Object> text(String phoneNumberId, String to, String body, boolean previewUrl, String replyTo) {
        Map<String, Object> payload = base(phoneNumberId, to, "text", replyTo);
        Map<String, Object> textObj = new LinkedHashMap<>();
        textObj.put("body", body);
        textObj.put("preview_url", previewUrl);
        payload.put("text", textObj);
        return payload;
    }

    public static Map<String, Object> image(String phoneNumberId, String to, String link, String mediaId, String caption, String replyTo) {
        Map<String, Object> payload = base(phoneNumberId, to, "image", replyTo);
        payload.put("image", mediaObject(link, mediaId, caption, null));
        return payload;
    }

    public static Map<String, Object> video(String phoneNumberId, String to, String link, String mediaId, String caption, String replyTo) {
        Map<String, Object> payload = base(phoneNumberId, to, "video", replyTo);
        payload.put("video", mediaObject(link, mediaId, caption, null));
        return payload;
    }

    public static Map<String, Object> audio(String phoneNumberId, String to, String link, String mediaId, String replyTo) {
        Map<String, Object> payload = base(phoneNumberId, to, "audio", replyTo);
        payload.put("audio", mediaObject(link, mediaId, null, null));
        return payload;
    }

    public static Map<String, Object> document(String phoneNumberId, String to, String link, String mediaId, String caption, String filename, String replyTo) {
        Map<String, Object> payload = base(phoneNumberId, to, "document", replyTo);
        payload.put("document", mediaObject(link, mediaId, caption, filename));
        return payload;
    }

    public static Map<String, Object> location(String phoneNumberId, String to, double latitude, double longitude, String name, String address, String replyTo) {
        Map<String, Object> payload = base(phoneNumberId, to, "location", replyTo);
        Map<String, Object> loc = new LinkedHashMap<>();
        loc.put("latitude", latitude);
        loc.put("longitude", longitude);
        if (name != null) {
            loc.put("name", name);
        }
        if (address != null) {
            loc.put("address", address);
        }
        payload.put("location", loc);
        return payload;
    }

    public static Map<String, Object> contacts(String phoneNumberId, String to, List<Map<String, Object>> contactsList, String replyTo) {
        Map<String, Object> payload = base(phoneNumberId, to, "contacts", replyTo);
        payload.put("contacts", fillWaIds(contactsList));
        return payload;
    }

    public static Map<String, Object> interactive(String phoneNumberId, String to, Map<String, Object> interactiveObj, String replyTo) {
        Map<String, Object> payload = base(phoneNumberId, to, "interactive", replyTo);
        payload.put("interactive", interactiveObj);
        return payload;
    }

    private static Map<String, Object> interactiveShell(String itype, String body, String header, String footer) {
        Map<String, Object> obj = new LinkedHashMap<>();
        obj.put("type", itype);
        obj.put("body", Map.of("text", body));
        if (header != null) {
            Map<String, Object> h = new LinkedHashMap<>();
            h.put("type", "text");
            h.put("text", header);
            obj.put("header", h);
        }
        if (footer != null) {
            obj.put("footer", Map.of("text", footer));
        }
        return obj;
    }

    private static String slugifyId(String title, int index) {
        String slug = (title == null ? "" : title).toLowerCase();
        slug = slug.replaceAll("[^a-z0-9]+", "_");
        slug = slug.replaceAll("^_+|_+$", "");
        return slug.isEmpty() ? "option_" + index : slug;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> interactiveButtons(String phoneNumberId, String to, String body, List<Object> buttons, String header, String footer, String replyTo) {
        if (buttons == null || buttons.isEmpty()) {
            throw new InvalidRequestException("At least one button is required");
        }
        if (buttons.size() > 3) {
            throw new InvalidRequestException("A maximum of 3 reply buttons is allowed");
        }

        List<Map<String, Object>> actionButtons = new ArrayList<>();
        for (int i = 0; i < buttons.size(); i++) {
            Object b = buttons.get(i);
            String title;
            String bid;
            if (b instanceof String s) {
                bid = slugifyId(s, i);
                title = s;
            } else {
                Map<String, Object> bm = (Map<String, Object>) b;
                title = bm.get("title") == null ? null : String.valueOf(bm.get("title"));
                Object id = bm.get("id");
                bid = (id != null && !"".equals(id)) ? String.valueOf(id) : slugifyId(title, i);
            }
            if (title == null || title.isEmpty()) {
                throw new InvalidRequestException("Button " + (i + 1) + ": title is required");
            }
            Map<String, Object> reply = new LinkedHashMap<>();
            reply.put("id", bid);
            reply.put("title", title);
            Map<String, Object> btn = new LinkedHashMap<>();
            btn.put("type", "reply");
            btn.put("reply", reply);
            actionButtons.add(btn);
        }

        Map<String, Object> obj = interactiveShell("button", body, header, footer);
        obj.put("action", Map.of("buttons", actionButtons));
        return interactive(phoneNumberId, to, obj, replyTo);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> interactiveList(String phoneNumberId, String to, String body, String button, List<Map<String, Object>> sections, String header, String footer, String replyTo) {
        if (button == null || button.isEmpty()) {
            throw new InvalidRequestException("A menu 'button' label is required");
        }
        if (sections == null || sections.isEmpty()) {
            throw new InvalidRequestException("At least one section is required");
        }

        List<Map<String, Object>> normSections = new ArrayList<>();
        for (int si = 0; si < sections.size(); si++) {
            Map<String, Object> section = sections.get(si);
            Object rowsObj = section.get("rows");
            List<Object> rows = rowsObj instanceof List ? (List<Object>) rowsObj : List.of();
            if (rows.isEmpty()) {
                throw new InvalidRequestException("Section " + (si + 1) + ": at least one row is required");
            }
            List<Map<String, Object>> normRows = new ArrayList<>();
            for (int ri = 0; ri < rows.size(); ri++) {
                Object row = rows.get(ri);
                String title;
                String rid;
                String desc;
                if (row instanceof String s) {
                    rid = slugifyId(s, ri);
                    title = s;
                    desc = null;
                } else {
                    Map<String, Object> rm = (Map<String, Object>) row;
                    title = rm.get("title") == null ? null : String.valueOf(rm.get("title"));
                    Object id = rm.get("id");
                    rid = (id != null && !"".equals(id)) ? String.valueOf(id) : slugifyId(title, ri);
                    desc = rm.get("description") == null ? null : String.valueOf(rm.get("description"));
                }
                if (title == null || title.isEmpty()) {
                    throw new InvalidRequestException("Section " + (si + 1) + " row " + (ri + 1) + ": title is required");
                }
                Map<String, Object> nr = new LinkedHashMap<>();
                nr.put("id", rid);
                nr.put("title", title);
                if (desc != null) {
                    nr.put("description", desc);
                }
                normRows.add(nr);
            }
            Map<String, Object> ns = new LinkedHashMap<>();
            ns.put("rows", normRows);
            if (section.get("title") != null) {
                ns.put("title", section.get("title"));
            }
            normSections.add(ns);
        }

        Map<String, Object> obj = interactiveShell("list", body, header, footer);
        Map<String, Object> action = new LinkedHashMap<>();
        action.put("button", button);
        action.put("sections", normSections);
        obj.put("action", action);
        return interactive(phoneNumberId, to, obj, replyTo);
    }

    public static Map<String, Object> interactiveCtaUrl(String phoneNumberId, String to, String body, String displayText, String url, String header, String footer, String replyTo) {
        if (displayText == null || displayText.isEmpty()) {
            throw new InvalidRequestException("'displayText' is required");
        }
        if (url == null || url.isEmpty()) {
            throw new InvalidRequestException("'url' is required");
        }
        Map<String, Object> obj = interactiveShell("cta_url", body, header, footer);
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("display_text", displayText);
        parameters.put("url", url);
        Map<String, Object> action = new LinkedHashMap<>();
        action.put("name", "cta_url");
        action.put("parameters", parameters);
        obj.put("action", action);
        return interactive(phoneNumberId, to, obj, replyTo);
    }

    public static Map<String, Object> template(String phoneNumberId, String to, String templateId, Object body, Object header, Object buttons) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("phone_number_id", phoneNumberId);
        payload.put("to", to);
        payload.put("template_id", templateId);
        if (body != null) {
            payload.put("body", body instanceof List ? body : List.of(body));
        }
        if (header != null) {
            payload.put("header", header);
        }
        if (buttons != null) {
            payload.put("buttons", buttons instanceof List ? buttons : List.of(buttons));
        }
        return payload;
    }

    public static Map<String, Object> readReceipt(String phoneNumberId, String messageId, boolean typing) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("phone_number_id", phoneNumberId);
        payload.put("message_id", messageId);
        payload.put("typing", typing);
        return payload;
    }
}