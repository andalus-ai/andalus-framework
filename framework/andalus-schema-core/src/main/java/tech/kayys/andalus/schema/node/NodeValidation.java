package tech.kayys.andalus.schema.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tech.kayys.andalus.schema.validator.ValidationIssue;

public class NodeValidation {
    public boolean isValid = true;
    public List<ValidationIssue> issues = new ArrayList<>();
    public Map<String, Object> metadata = new HashMap<>();
}