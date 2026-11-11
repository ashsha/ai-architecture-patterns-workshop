package os.expert.setup;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Offline check that every provider file in config/providers produces exactly one ChatModel
 * of the expected class. Each provider runs in a fresh JVM because the LangChain4j CDI
 * extension reads its configuration once per JVM. No network call and no real credential is used.
 */
class ProviderConfigurationTest {

    private static final Map<String, String> DUMMY_SETTINGS = Map.of(
            "GEMINI_API_KEY", "dummy",
            "OPENAI_API_KEY", "dummy",
            "OPENAI_MODEL", "test-model",
            "ANTHROPIC_API_KEY", "dummy",
            "ANTHROPIC_MODEL", "test-model",
            "OPENAI_COMPATIBLE_BASE_URL", "http://localhost:1/v1",
            "OPENAI_COMPATIBLE_MODEL", "test-model",
            "OLLAMA_MODEL", "test-model");

    @ParameterizedTest
    @CsvSource({
        "gemini,GoogleAiGeminiChatModel",
        "openai,OpenAiChatModel",
        "anthropic,AnthropicChatModel",
        "openai-compatible,OpenAiChatModel",
        "ollama,OllamaChatModel"
    })
    void selectsExactlyOneChatModel(String provider, String expectedClass) throws IOException, InterruptedException {
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        ProcessBuilder builder = new ProcessBuilder(List.of(
                java,
                "-Dworkshop.ai.provider=" + provider,
                "-cp", System.getProperty("java.class.path"),
                ProviderProbe.class.getName()));
        builder.environment().putAll(DUMMY_SETTINGS);
        builder.redirectErrorStream(true);

        Process process = builder.start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertThat(process.waitFor(60, TimeUnit.SECONDS)).isTrue();

        assertThat(output).contains("PROBE_BEANS=1").contains("PROBE_MODEL=" + expectedClass);
    }
}
