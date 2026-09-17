package tech.kayys.wayang.harness.context;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default implementation of {@link ContextAssembler} gathering contributions, sorting by priority, and applying token budgeting.
 */
public class DefaultContextAssembler implements ContextAssembler {

    private final List<ContextProvider> providers = new CopyOnWriteArrayList<>();

    public DefaultContextAssembler(List<ContextProvider> initialProviders) {
        if (initialProviders != null) {
            providers.addAll(initialProviders);
        }
    }

    public void register(ContextProvider provider) {
        Objects.requireNonNull(provider, "provider");
        providers.add(provider);
    }

    @Override
    public AssembledContext assemble(ContextRequest request) {
        Objects.requireNonNull(request, "request");
        List<ContextItem> allItems = new ArrayList<>();

        for (ContextProvider provider : providers) {
            ContextContribution contribution = provider.provide(request);
            if (contribution != null && contribution.items() != null) {
                allItems.addAll(contribution.items());
            }
        }

        // Sort by priority (CRITICAL -> HIGH -> NORMAL -> LOW -> OPTIONAL)
        allItems.sort(Comparator.comparingInt(item -> item.priority().ordinal()));

        // Filter and budget tokens (simple heuristic: 4 chars ~ 1 token)
        List<ContextItem> budgetedItems = new ArrayList<>();
        long currentTokens = 0;

        for (ContextItem item : allItems) {
            long itemTokens = Math.max(1, item.content().length() / 4);
            if (currentTokens + itemTokens <= request.maxTokens() || item.priority() == ContextPriority.CRITICAL) {
                budgetedItems.add(item);
                currentTokens += itemTokens;
            }
        }

        return new DefaultAssembledContext(ContextId.generate(), budgetedItems, Map.of("totalCandidates", allItems.size()), currentTokens);
    }
}
