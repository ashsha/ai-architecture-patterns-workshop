package os.expert.setup;

import dev.langchain4j.model.chat.ChatModel;
import jakarta.enterprise.context.spi.Context;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import java.util.Set;

/** Starts CDI in a fresh JVM and prints the number and class of the ChatModel beans. No network call. */
public class ProviderProbe {

    public static void main(String[] args) {
        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {
            BeanManager beanManager = container.getBeanManager();
            Set<Bean<?>> beans = beanManager.getBeans(ChatModel.class);
            System.out.println("PROBE_BEANS=" + beans.size());
            @SuppressWarnings("unchecked")
            Bean<Object> bean = (Bean<Object>) beans.iterator().next();
            Context context = beanManager.getContext(bean.getScope());
            Object model = context.get(bean, beanManager.createCreationalContext(bean));
            System.out.println("PROBE_MODEL=" + model.getClass().getSimpleName());
        }
    }
}
