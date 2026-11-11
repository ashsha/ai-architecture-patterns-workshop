# OpenAI-Compatible APIs

!!! warning "Not yet tested"
    Built and checked offline only. No lab has been run against any compatible service.

## 1. Why choose this provider?

- Your organization exposes models through a gateway or a service with an OpenAI-style chat-completions API.
- You run a compatible server yourself.

API compatibility does not guarantee feature compatibility. A service may accept chat requests yet not support structured output or tool calling the way OpenAI does.

## 2. Requirements

- The service's base URL, **including its version path** (for example `https://host/v1`).
- A model identifier as that service names it.
- An API key, if the service requires one.

## 3. Maven dependency

The same OpenAI integration is used (already in `pom.xml`, version from the BOM, 1.22.0):

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
</dependency>
```

## 4. Environment variables

```dotenv
WORKSHOP_AI_PROVIDER=openai-compatible
OPENAI_COMPATIBLE_BASE_URL=https://your-host/v1
OPENAI_COMPATIBLE_MODEL=your-model-id
# only if the service checks it; otherwise a placeholder is sent
# OPENAI_COMPATIBLE_API_KEY=your-key
```

## 5. MicroProfile Config configuration

`config/providers/openai-compatible.properties` uses `OpenAiChatModel` with a custom `base-url`:

```properties
dev.langchain4j.cdi.plugin.chat-model.class=dev.langchain4j.model.openai.OpenAiChatModel
dev.langchain4j.cdi.plugin.chat-model.config.base-url=${OPENAI_COMPATIBLE_BASE_URL}
dev.langchain4j.cdi.plugin.chat-model.config.api-key=${OPENAI_COMPATIBLE_API_KEY:not-needed}
dev.langchain4j.cdi.plugin.chat-model.config.model-name=${OPENAI_COMPATIBLE_MODEL}
dev.langchain4j.cdi.plugin.chat-model.config.temperature=0.2
dev.langchain4j.cdi.plugin.chat-model.config.max-retries=0
dev.langchain4j.cdi.plugin.chat-model.config.timeout=PT60S
```

## 6. Test the connection

```bash
mvn -q compile
mvn exec:java -Dexec.mainClass=os.expert.setup.ProviderConnectionCheck
```

This confirms basic inference only. See [Getting Started](../getting-started.md#7-test-the-connection).

## 7. Known limitations

- Not yet tested for any lab, on any service.
- Differences between services: supported parameters, structured output, tool-calling format, streaming, error codes, and model names.
- Lab 5 needs reliable multi-step tool calling; verify it explicitly.

## 8. Troubleshooting

| Symptom | Suggestion |
|---|---|
| HTTP 404 | Check the base URL and its version path (`/v1`). |
| HTTP 401 | Set `OPENAI_COMPATIBLE_API_KEY`. |
| Model not found | Use the model id exactly as the service lists it. |
| Connection refused / timeout | The service is not reachable or not running. |
| Errors about unsupported parameters | The service rejects an option in the file; remove it for that service. |

## 9. Return to Lab 1

[Continue to Lab 1](../labs/lab1-hello-world.md) or [choose another provider](index.md).
