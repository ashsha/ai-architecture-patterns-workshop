# Getting Started

Prepare your environment before starting the labs. This takes about 15–20 minutes.

**Your journey:** Clone Repository → Choose Provider → Configure Credentials → Test Connection → Start Lab 1.

!!! tip "Choose your AI provider"
    The workshop works with Gemini, OpenAI, Anthropic Claude, OpenAI-compatible APIs, and local Ollama models. Gemini is the default and the only provider verified end to end. See [Model Providers](providers/index.md) to pick one.

## 1. Workshop prerequisites

- Working knowledge of Java and object-oriented design
- Basic familiarity with Maven and Git
- API access to one supported AI provider, or a local Ollama model (Gemini is the default; a Google account is enough to get a key)
- A stable internet connection

## 2. Required software

| Tool | Purpose |
|---|---|
| Java Development Kit (JDK) | Java 21 or newer is required to compile and run the labs. |
| Apache Maven | Build and dependency management |
| IntelliJ IDEA (or another Java IDE) | Development environment |
| Git | Clone the repository |
| An AI provider | Google Gemini (default), OpenAI, Anthropic, an OpenAI-compatible API, or Ollama. See [Model Providers](providers/index.md). |

The labs use **LangChain4j** and **CDI**; their dependencies are managed by Maven and require no separate installation.

## 3. Clone the repository

```bash
git clone https://github.com/O-S-Expert/ai-architecture-patterns-workshop.git
cd ai-architecture-patterns-workshop
mvn clean install
```

## 4. Open the project in IntelliJ IDEA

1. Choose **File → Open** and select the repository folder.
2. Let the IDE import the Maven project once the Java build is available.
3. Configure the project SDK to your installed JDK.

## 5. Choose a provider

Read the [Model Providers overview](providers/index.md) and pick one guide: [Gemini](providers/gemini.md) (default), [OpenAI](providers/openai.md), [Anthropic Claude](providers/anthropic.md), [OpenAI-compatible APIs](providers/openai-compatible.md), or [Ollama](providers/ollama.md). To stay with Gemini, create a key in [Google AI Studio](https://aistudio.google.com/api-keys).

A subscription to a chat product does not give you API access. Verify API access, credits, and model capabilities first.

The lab code does not change with the provider. Only configuration does.

## 6. Configure credentials

Copy the template and fill in the variables for your provider in the local `.env` file at the repository root. It is ignored by Git.

```bash
cp .env.example .env
# Gemini (default):
# GEMINI_API_KEY=your-gemini-api-key
# Another provider: also set WORKSHOP_AI_PROVIDER, for example
# WORKSHOP_AI_PROVIDER=openai
# OPENAI_API_KEY=...   OPENAI_MODEL=...
```

!!! warning "Never commit credentials"
    Do not place API keys in source files, committed configuration, or screenshots. Keep them in the git-ignored `.env` file or in environment variables.

`workshop.ai.provider` (default `gemini`) selects which file in `config/providers/` is loaded. SmallRye Config reads `.env` from the working directory, so start the application from the repository root. A real environment variable takes precedence over the file.

## 7. Test the connection

Check the installed tools and build:

```bash
java -version
mvn -version
git --version
mvn clean verify
```

Then run the provider-independent smoke test. From IntelliJ IDEA, run `os.expert.setup.ProviderConnectionCheck` (working directory: the repository root), or:

```bash
mvn -q compile exec:java -Dexec.mainClass=os.expert.setup.ProviderConnectionCheck
```

Expected output with Gemini (the model's reply may vary slightly):

```text
Provider selected : gemini
Model class       : dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
Response          : OK
SUCCESS: the model is reachable (basic inference only).
```

`os.expert.setup.ProviderConnectionCheck`:

```java
package os.expert.setup;

import dev.langchain4j.model.chat.ChatModel;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class ProviderConnectionCheck {

    private static final String PREFIX = "dev.langchain4j.cdi.plugin.chat-model.";

    public static void main(String[] args) {
        Config config = ConfigProvider.getConfig();
        String provider = config.getOptionalValue("workshop.ai.provider", String.class).orElse("(not set)");
        System.out.println("Provider selected : " + provider);
        System.out.println("Model class       : " + config.getOptionalValue(PREFIX + "class", String.class).orElse("(none)"));

        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {
            Instance<ChatModel> models = container.select(ChatModel.class);
            if (models.isUnsatisfied()) {
                System.err.println("FAILED: no ChatModel bean. Check WORKSHOP_AI_PROVIDER, that"
                        + " config/providers/" + provider + ".properties exists, and that you run from the repository root.");
                System.exit(1);
            }
            String answer = models.get().chat("Reply with the single word: OK");
            System.out.println("Response          : " + answer);
            System.out.println("SUCCESS: the model is reachable (basic inference only).");
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            System.err.println("FAILED: " + root.getClass().getSimpleName() + ": " + root.getMessage());
            System.err.println("Hints: missing API key or model variable, wrong provider name, model not available to your key,"
                    + " Ollama not running, or a network problem.");
            System.exit(1);
        }
    }
}
```

It starts CDI, asks for the one configured `ChatModel`, sends a tiny request, and prints the result without showing any key. A success confirms basic inference only. It does not validate structured output or multi-step tool calling.

!!! note "Lab 0 (Gemini)"
    `os.expert.lab0.App` is a Gemini-based configuration check and still works with the default provider. Gemini's model name is set in `config/providers/gemini.properties` (`GEMINI_MODEL`). If a model returns HTTP 404 or times out, choose another one available to your key.

## 8. Troubleshooting

| Symptom | Suggestion |
|---|---|
| `java` or `mvn` not found | Verify installation and that the tools are on your `PATH`. |
| API key not detected | Check `.env` exists in the repository root and the app is started from that directory. |
| HTTP 404 for a model | The model is unavailable to your key; change the model variable (for Gemini, `GEMINI_MODEL`). |
| HTTP 429/503 or request timeout | Rate limit or high demand; retry later or use another model. |
| Authentication or quota errors | Check the key and credits with your provider. |
| `no ChatModel bean` | Check `WORKSHOP_AI_PROVIDER` and that you run from the repository root. |
| Provider-specific problems | See the troubleshooting section of your [provider guide](providers/index.md). |
| Corporate proxy or firewall | Ensure access to your provider's API endpoints. |

## 9. Continue to Lab 1

Once the connection test succeeds, continue to [Lab 1: Hello World](labs/lab1-hello-world.md), or review the [Labs overview](labs/index.md).
