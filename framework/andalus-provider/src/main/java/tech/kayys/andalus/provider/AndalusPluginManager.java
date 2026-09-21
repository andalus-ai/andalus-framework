package tech.kayys.andalus.provider;

import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import tech.kayys.andalus.inference.InferenceProvider;

/**
 * Utility for dynamically discovering and loading InferenceProvider plugins from ~/.andalus/plugins/
 */
public class AndalusPluginManager {
    private static final Logger LOG = Logger.getLogger(AndalusPluginManager.class);
    private static final AndalusPluginManager INSTANCE = new AndalusPluginManager();

    private final Path pluginDirectory;
    private final List<InferenceProvider> loadedProviders = new ArrayList<>();
    private URLClassLoader pluginClassLoader;

    public static AndalusPluginManager getInstance() {
        return INSTANCE;
    }

    private AndalusPluginManager() {
        String home = System.getProperty("user.home");
        this.pluginDirectory = Paths.get(home, ".andalus", "plugins");
        try {
            Files.createDirectories(this.pluginDirectory);
        } catch (IOException e) {
            LOG.error("Failed to create andalus plugins directory", e);
        }
    }

    /**
     * Scans the plugin directory for JARs and loads Provider implementations via ServiceLoader.
     */
    public synchronized void loadPlugins() {
        if (!Files.exists(pluginDirectory)) {
            return;
        }

        List<URL> jarUrls = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(pluginDirectory, 1)) {
            stream.filter(p -> p.toString().endsWith(".jar"))
                  .forEach(p -> {
                      try {
                          jarUrls.add(p.toUri().toURL());
                          LOG.infof("Discovered provider plugin jar: %s", p.getFileName());
                      } catch (Exception e) {
                          LOG.errorf(e, "Failed to build URL for plugin jar: %s", p);
                      }
                  });
        } catch (IOException e) {
            LOG.error("Failed to scan andalus plugins directory", e);
            return;
        }

        if (jarUrls.isEmpty()) {
            LOG.info("No provider plugins found in ~/.andalus/plugins");
            return;
        }

        pluginClassLoader = new URLClassLoader(
                jarUrls.toArray(new URL[0]),
                Thread.currentThread().getContextClassLoader()
        );

        // Load InferenceProvider interface implementations
        ServiceLoader<InferenceProvider> serviceLoader = ServiceLoader.load(InferenceProvider.class, pluginClassLoader);
        loadedProviders.clear();
        for (InferenceProvider provider : serviceLoader) {
            loadedProviders.add(provider);
            LOG.infof("Loaded inference provider plugin: %s", provider.id());
            try {
                provider.initialize();
                LOG.infof("Initialized inference provider: %s", provider.metadata().name());
            } catch (Exception e) {
                LOG.errorf(e, "Failed to initialize provider: %s", provider.id());
            }
        }
    }

    public List<InferenceProvider> getLoadedProviders() {
        return new ArrayList<>(loadedProviders);
    }
}
