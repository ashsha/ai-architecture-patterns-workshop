package os.expert.setup;

import dev.langchain4j.model.chat.ChatModel;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Provider-independent smoke test: starts CDI, obtains the configured {@link ChatModel}
 * and sends one small request. It confirms basic inference only; it does not validate
 * structured output or multi-step tool calling.
 */
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
