# Local Models (Ollama)

!!! warning "Not yet tested"
    Built and checked offline only. No lab has been run against an Ollama model, and no model is recommended yet.

## 1. Why choose this provider?

- No cloud API key and no cloud inference charges.
- Your data stays on your machine.

Local models still need adequate hardware, and small models are often weaker at structured output and tool calling.

## 2. Requirements

- [Ollama](https://ollama.com/download) installed and running.
- Enough RAM/VRAM for the model you choose; larger models need substantially more.
- A model that supports **tool calling** (check its page in the Ollama library) if you want to attempt Lab 5.

## 3. Maven dependency

Already in `pom.xml` (managed by the LangChain4j BOM, 1.22.0):

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-ollama</artifactId>
</dependency>
```

## 4. Environment variables

Install, start, and pull a model:

```bash
ollama serve            # skip if the desktop app already runs it
ollama pull <model>
ollama list
```

```dotenv
WORKSHOP_AI_PROVIDER=ollama
OLLAMA_MODEL=<model-you-pulled>
# optional, default http://localhost:11434
# OLLAMA_BASE_URL=http://localhost:11434
```

## 5. MicroProfile Config configuration

`config/providers/ollama.properties`:

```properties
dev.langchain4j.cdi.plugin.chat-model.class=dev.langchain4j.model.ollama.OllamaChatModel
dev.langchain4j.cdi.plugin.chat-model.config.base-url=${OLLAMA_BASE_URL:http://localhost:11434}
dev.langchain4j.cdi.plugin.chat-model.config.model-name=${OLLAMA_MODEL}
dev.langchain4j.cdi.plugin.chat-model.config.temperature=0.2
dev.langchain4j.cdi.plugin.chat-model.config.max-retries=0
dev.langchain4j.cdi.plugin.chat-model.config.timeout=PT120S
```

The longer timeout allows for slow local inference.

## 6. Test the connection

```bash
mvn -q compile
mvn exec:java -Dexec.mainClass=os.expert.setup.ProviderConnectionCheck
```

This confirms basic inference only. See [Getting Started](../getting-started.md#7-test-the-connection).

## 7. Known limitations

- Not every Ollama model supports tool calling; Lab 5 may fail or loop with those that do not.
- Structured output (Labs 1, 4) can be unreliable on small models.
- Speed depends on your hardware.
- No model has been verified for the five labs, so none is recommended.

## 8. Troubleshooting

| Symptom | Suggestion |
|---|---|
| Connection refused | Ollama is not running; start it and check `OLLAMA_BASE_URL`. |
| Model not found | Run `ollama pull <model>`; the name must match `ollama list`. |
| Timeout | Use a smaller model or raise `timeout`. |
| Startup fails with an unresolvable property | `OLLAMA_MODEL` is not set. |
| Malformed JSON in Labs 1/4, or Lab 5 ignores tools | The model is too weak for that lab; try another. |

## 9. Return to Lab 1

[Continue to Lab 1](../labs/lab1-hello-world.md) or [choose another provider](index.md).
