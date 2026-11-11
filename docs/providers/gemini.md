# Google Gemini

Gemini is the workshop's default provider and the only one verified end to end.

## 1. Why choose this provider?

- It is the configuration all five labs were developed and run against.
- It needs no changes: `workshop.ai.provider` defaults to `gemini`.

## 2. Requirements

- A Gemini API key from [Google AI Studio](https://aistudio.google.com/api-keys).
- A model available to that key. Availability varies by account and changes over time; for example, `gemini-2.5-flash` returned HTTP 404 for new users, so it is not used.

## 3. Maven dependency

Already in `pom.xml` (version managed by the LangChain4j BOM, 1.22.0):

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-google-ai-gemini</artifactId>
</dependency>
```

## 4. Environment variables

In `.env` at the repository root (git-ignored):

```dotenv
GEMINI_API_KEY=your-gemini-api-key
# optional, default is gemini-3.8-flash
# GEMINI_MODEL=another-model-available-to-your-key
```

## 5. MicroProfile Config configuration

`config/providers/gemini.properties` (loaded when `workshop.ai.provider=gemini`):

```properties
dev.langchain4j.cdi.plugin.chat-model.class=dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
dev.langchain4j.cdi.plugin.chat-model.config.api-key=${GEMINI_API_KEY}
dev.langchain4j.cdi.plugin.chat-model.config.model-name=${GEMINI_MODEL:gemini-3.8-flash}
dev.langchain4j.cdi.plugin.chat-model.config.temperature=0.2
dev.langchain4j.cdi.plugin.chat-model.config.max-retries=0
dev.langchain4j.cdi.plugin.chat-model.config.timeout=PT60S
dev.langchain4j.cdi.plugin.chat-model.config.return-thinking=true
dev.langchain4j.cdi.plugin.chat-model.config.send-thinking=true
```

`return-thinking` and `send-thinking` preserve Gemini **thought signatures**, which Gemini needs to continue a multi-step tool-calling conversation (Lab 5). They exist only on the Gemini builder; do not copy them to other providers.

## 6. Test the connection

From the repository root:

```bash
mvn -q compile
mvn exec:java -Dexec.mainClass=os.expert.setup.ProviderConnectionCheck
```

See [Getting Started](../getting-started.md#7-test-the-connection) for the full class and IntelliJ instructions.

## 7. Known limitations

- Verified: Labs 1–5 with `langchain4j-google-ai-gemini` 1.22.0 and `gemini-3.8-flash`. Other Gemini models are not covered by that statement.
- Structured output (Labs 1 and 4) and tool calling (Lab 5) depend on the model. Verify them if you change `GEMINI_MODEL`.
- Rate limits and quotas depend on your account and the model; check Google's current documentation. Retries are disabled (`max-retries=0`) so failures show up immediately.

## 8. Troubleshooting

| Symptom | Suggestion |
|---|---|
| HTTP 404 for the model | The model is not available to your key. Set `GEMINI_MODEL` to one that is. |
| HTTP 401/403 | Key invalid or lacks access; check it in Google AI Studio. |
| HTTP 429/503 | Rate limit or high demand; retry later. |
| `GEMINI_API_KEY` not found | `.env` must be in the working directory; run from the repository root. |
| Lab 5 stops after the first tool call | Check that `return-thinking` and `send-thinking` are still `true`. |

## 9. Return to Lab 1

[Continue to Lab 1](../labs/lab1-hello-world.md) or [choose another provider](index.md).
