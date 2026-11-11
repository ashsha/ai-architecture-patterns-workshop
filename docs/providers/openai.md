# OpenAI

!!! warning "Not yet tested"
    This configuration is built and checked offline (the right `ChatModel` class is created). No lab has been run against OpenAI in this workshop.

## 1. Why choose this provider?

- You already have OpenAI **API** access and credits.
- You want to run the same labs with an OpenAI model.

A ChatGPT subscription and OpenAI API billing are separate. A ChatGPT plan does not give a Java application API access.

## 2. Requirements

- An OpenAI API key with available credits.
- A model id your account can use. The workshop sets no default because model ids are retired and replaced; choose one from OpenAI's current model list. Prefer a model that supports structured output and tool calling.

## 3. Maven dependency

Already in `pom.xml` (managed by the LangChain4j BOM, 1.22.0):

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
</dependency>
```

## 4. Environment variables

```dotenv
WORKSHOP_AI_PROVIDER=openai
OPENAI_API_KEY=your-openai-api-key
OPENAI_MODEL=your-model-id
```

## 5. MicroProfile Config configuration

`config/providers/openai.properties`:

```properties
dev.langchain4j.cdi.plugin.chat-model.class=dev.langchain4j.model.openai.OpenAiChatModel
dev.langchain4j.cdi.plugin.chat-model.config.api-key=${OPENAI_API_KEY}
dev.langchain4j.cdi.plugin.chat-model.config.model-name=${OPENAI_MODEL}
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

- Not yet tested for any lab. Do not assume Gemini's output.
- **Structured output (Labs 1, 4):** this configuration does not enable JSON-schema response format; LangChain4j then falls back to prompt-based JSON. Results depend on the model.
- **Tool calling (Lab 5):** model-dependent. Check it with the multi-step order-cancellation flow.
- Gemini-only properties (`return-thinking`, `send-thinking`) are intentionally absent.

## 8. Troubleshooting

| Symptom | Suggestion |
|---|---|
| Startup fails with an unresolvable property | `OPENAI_API_KEY` or `OPENAI_MODEL` is not set. |
| HTTP 401 | Invalid key, or the key's project lacks access. |
| HTTP 404 / model not found | Your account cannot use that model id. |
| HTTP 429 | Rate limit or no remaining credit; check your billing. |
| `no ChatModel bean` | `WORKSHOP_AI_PROVIDER` is misspelled or you are not in the repository root. |

## 9. Return to Lab 1

[Continue to Lab 1](../labs/lab1-hello-world.md) or [choose another provider](index.md).
