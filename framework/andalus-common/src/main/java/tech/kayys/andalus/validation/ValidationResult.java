package tech.kayys.andalus.validation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validation Result
 */
public record ValidationResult(
    boolean valid,
    List<ValidationError> errors,
    List<ValidationWarning> warnings,
    Map<String, Object> metadata
) {
    public static ValidationResult success() {
        return new ValidationResult(true, List.of(), List.of(), Map.of());
    }
    
    public static ValidationResult error(String code, String message) {
        return new ValidationResult(false, 
            List.of(new ValidationError(code, message, null)), 
            List.of(), 
            Map.of()
        );
    }
    
    public static ValidationResult error(String code, String message, String path) {
        return new ValidationResult(false, 
            List.of(new ValidationError(code, message, path)), 
            List.of(), 
            Map.of()
        );
    }
    
    public ValidationResult withError(ValidationError error) {
        List<ValidationError> newErrors = new ArrayList<>(errors);
        newErrors.add(error);
        return new ValidationResult(false, newErrors, warnings, metadata);
    }
    
    public ValidationResult withWarning(ValidationWarning warning) {
        List<ValidationWarning> newWarnings = new ArrayList<>(warnings);
        newWarnings.add(warning);
        return new ValidationResult(valid, errors, newWarnings, metadata);
    }
}
