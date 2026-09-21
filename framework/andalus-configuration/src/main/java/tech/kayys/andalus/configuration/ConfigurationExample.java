package tech.kayys.andalus.configuration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.nio.file.*;

public class ConfigurationExample {
    
    public static void main(String[] args) throws Exception {
        // 1. Create registry
        ConfigurationRegistry registry = new ConfigurationRegistry();
        
        // 2. Load from file
        ConfigurationResource fileConfig = registry.loadFromFile(Paths.get("config/andalus.yaml"));
        
        // 3. Load from environment
        ConfigurationResource envConfig = registry.loadFromEnvironment("ANDALUS_");
        
        // 4. Merge configurations
        ConfigurationResource merged = registry.merge(fileConfig, envConfig);
        
        // 5. Get active configuration
        ConfigurationResource config = registry.getActive();
        
        // 6. Read values
        int maxThreads = config.get("andalus.runtime.maxThreads", Integer.class, 200);
        String apiKey = config.get("andalus.models.openai.apiKey", String.class);
        
        // 7. Get section
        ConfigurationResource runtimeConfig = config.getSection("andalus.runtime");
        int timeout = runtimeConfig.get("defaultTimeout", Integer.class, 30000);
        
        // 8. Update configuration (creates new version)
        ConfigurationResource updated = config
            .withValue("andalus.runtime.maxThreads", 500)
            .withStatus(ConfigStatus.MODIFIED);
        registry.register(updated);
        
        // 9. Set active
        registry.setActive(updated.id());
        
        // 10. Watch for changes
        ConfigurationWatcher watcher = new ConfigurationWatcher(registry);
        watcher.addListener(event -> {
            System.out.println("Configuration changed: " + event.path());
            System.out.println("Old: " + event.oldValues());
            System.out.println("New: " + event.newValues());
        });
        watcher.start();
        
        // 11. Create tenant config
        ConfigurationResource tenantConfig = registry.createTenantConfig("tenant-123", merged);
        int tenantMaxThreads = tenantConfig.get("andalus.runtime.maxThreads", Integer.class, 200);
        
        System.out.println("Configuration loaded!");
        System.out.println("Max threads: " + maxThreads);
        System.out.println("Tenant max threads: " + tenantMaxThreads);
    }
}