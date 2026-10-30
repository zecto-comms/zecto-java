# Zecto Java SDK

Java SDK for the Zecto multi-channel messaging platform.

## Requirements

- Java 17 or newer
- Maven

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.zecto</groupId>
    <artifactId>zecto-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

Or with Gradle:

```kotlin
implementation("io.zecto:zecto-java:0.1.0")
```

## Quick start

```java
import io.zecto.ZectoClient;
import java.util.Map;

ZectoClient client = new ZectoClient("your-api-key");

Map<String, Object> res = client.whatsapp.messages.sendText(
    "PHONE_NUMBER_ID",
    "923001234567",
    "Hello from Zecto"
);

System.out.println(res.get("message_id"));
```

## Configuration

The client takes the API key, and optionally a timeout in seconds:

```java
ZectoClient client = new ZectoClient("your-api-key");
ZectoClient client = new ZectoClient("your-api-key", 60.0);
```

| Argument  | Required | Default                   | Description                    |
| --------- | -------- | ------------------------- | ------------------------------ |
| `apiKey`  | yes      | none                      | Your Zecto API key             |
| `timeout` | no       | `30.0`                    | Request timeout in seconds     |

Keep your API key out of source control. Load it from an environment variable or a configuration file that is not committed.

## Messages

All message methods are under `client.whatsapp.messages`. Methods with optional parameters provide short overloads for the common case and a full overload for all parameters.

```java
var m = client.whatsapp.messages;

m.sendText("PID", "923001234567", "Plain text");
m.sendText("PID", "923001234567", "With link https://example.com", true);

m.sendImage("PID", "923001234567", "https://example.com/image.jpg");
m.sendImage("PID", "923001234567", "https://example.com/image.jpg", null, "A caption", null);

m.sendVideo("PID", "923001234567", "https://example.com/video.mp4");
m.sendAudio("PID", "923001234567", "https://example.com/audio.mp3");
m.sendDocument("PID", "923001234567", "https://example.com/file.pdf");

m.sendLocation("PID", "923001234567", 31.5204, 74.3587);

m.sendContacts("PID", "923001234567", List.of(
    Map.of(
        "name", Map.of("formatted_name", "Jane Doe", "first_name", "Jane"),
        "phones", List.of(Map.of("phone", "+92 300 1234567"))
    )
));

m.sendButtons("PID", "923001234567", "Please choose:", List.of("Yes", "No", "Maybe"));

m.sendList("PID", "923001234567", "Choose an option", "Open menu", List.of(
    Map.of(
        "title", "Section 1",
        "rows", List.of(
            Map.of("title", "Option A", "description", "First option"),
            Map.of("title", "Option B", "description", "Second option")
        )
    )
));

m.sendCtaUrl("PID", "923001234567", "Visit our site", "Open site", "https://example.com");

m.sendTemplate("PID", "923001234567", "TEMPLATE_ID",
    List.of("value1"), null, List.of("yes", "PROMO2026"));

m.markRead("PID", "MESSAGE_ID");

m.list(Map.of("status", "sent", "limit", 20));

m.get("MESSAGE_ID");
```

For media, provide either a link or a media id. A maximum of three buttons is allowed.

## Templates

```java
client.whatsapp.templates.list();
client.whatsapp.templates.list("approved", "MARKETING", "otp");
client.whatsapp.templates.exampleConfig("TEMPLATE_ID");
```

## Numbers

```java
client.whatsapp.numbers.list();
client.whatsapp.numbers.get("PHONE_NUMBER_ID");
```

## Account

```java
client.whatsapp.account.get();
```

## Flows

```java
client.whatsapp.flows.list();
client.whatsapp.flows.list("published");
client.whatsapp.flows.rename("FLOW_ID", "New name");
client.whatsapp.flows.setStatus("FLOW_ID", "published");
client.whatsapp.flows.trigger("FLOW_ID", "923001234567");
```

## Error handling

All errors extend `io.zecto.exceptions.ZectoException` (an unchecked exception). API responses map to specific classes by status code.

```java
import io.zecto.exceptions.AuthenticationException;
import io.zecto.exceptions.ValidationException;
import io.zecto.exceptions.RateLimitException;

try {
    client.whatsapp.messages.sendText("PID", "923001234567", "Hi");
} catch (AuthenticationException e) {
    // invalid API key
} catch (ValidationException e) {
    // malformed request payload
} catch (RateLimitException e) {
    // slow down
}
```

| Class                       | Trigger                            |
| --------------------------- | ---------------------------------- |
| `InvalidRequestException`   | Client-side validation before send |
| `APIConnectionException`    | Network failure or timeout         |
| `ValidationException`       | HTTP 400 / 422                      |
| `AuthenticationException`   | HTTP 401                            |
| `PermissionDeniedException` | HTTP 403                            |
| `NotFoundException`         | HTTP 404                            |
| `ConflictException`         | HTTP 409                            |
| `RateLimitException`        | HTTP 429                            |
| `UpstreamException`         | HTTP 502                            |
| `ServerException`           | Other 5xx                          |
| `APIException`              | Base class for all HTTP errors     |
| `ZectoException`            | Base class for all SDK errors      |

`APIException` exposes `getStatusCode()`, `getDetail()`, and `getBody()`.

## Development

```
mvn compile
mvn test
mvn package
```

## License

MIT
