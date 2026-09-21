package tech.kayys.andalus.database;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;



/**
 * Page Request
 */
public record PageRequest(int page, int size, String sortBy, boolean ascending) {
    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size, null, true);
    }
    
    public static PageRequest of(int page, int size, String sortBy) {
        return new PageRequest(page, size, sortBy, true);
    }
    
    public int offset() {
        return page * size;
    }
}
