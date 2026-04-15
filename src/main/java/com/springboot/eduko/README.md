## `handleExceptions` package documentation

This package centralizes exception handling and error response formatting for the Spring Boot application.

It uses:
- Spring MVC `@ControllerAdvice` to intercept exceptions across controllers.
- A small DTO (`Response`) to return localized messages (English and Arabic).
- Spring `ResourceBundleMessageSource` to load messages from `.properties` bundles.

## Components

### `CustomException`
Global exception handler (`@ControllerAdvice`).

It currently defines two handlers:
- `handleException(Exception e)`
  - Trigger: any uncaught `Exception` (fallback).
  - HTTP status: `406 NOT_ACCEPTABLE`.
  - Response body: a single `Response` built from `e.getMessage()` (treated as a message key).

- `handleException(MethodArgumentNotValidException e)`
  - Trigger: validation failure for a controller method argument (`MethodArgumentNotValidException`), typically from `@Valid`.
  - HTTP status: `400 BAD_REQUEST`.
  - Response body: `List<Response>`, one per field validation error.
  - Message selection: it uses `error.getDefaultMessage()` as the message key for the resource bundle.

Both handlers delegate message translation to `BundleService`.

### `Response`
Simple response DTO with localized message fields:
- `messageEn`: English message
- `messageAr`: Arabic message

Lombok annotations (`@Getter`, `@Setter`, `@AllArgsConstructor`, `@NoArgsConstructor`) generate boilerplate methods automatically.

### `Master`
Spring configuration class that creates the message source bean.

It:
- Reads `spring.messages.basename` via `@Value`
- Exposes a `ResourceBundleMessageSource` bean configured with:
  - `setBasename(baseName)`
  - `setDefaultEncoding("UTF-8")`

This message source is used by `BundleService` to resolve message keys for different locales.

### `BundleService`
Service that converts a message key into a localized `Response`.

It provides:
- `getMessageEn(String key)` -> resolves the key with locale `en`
- `getMessageAr(String key)` -> resolves the key with locale `ar`
- `getResponse(String key)` -> returns `new Response(getMessageEn(key), getMessageAr(key))`

## Message bundle expectations

This package assumes that:
- the strings passed as “keys” (from `Exception.getMessage()` or validation `error.getDefaultMessage()`) correspond to message entries in your resource bundles configured by `spring.messages.basename`.
- you have locale-specific bundles available for `en` and `ar`.

## Example error responses

Validation error example (`MethodArgumentNotValidException`):
```json
[
  { "messageEn": "....", "messageAr": "...." },
  { "messageEn": "....", "messageAr": "...." }
]
```

Generic exception example (`Exception` fallback):
```json
{ "messageEn": "....", "messageAr": "...." }
```

