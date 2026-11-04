package tech.kayys.andalus.schema.workflow.catalog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Validates catalog metadata before it is exposed to a workflow designer. */
public class NodeCapabilityCatalogValidator {

    public List<String> validate(NodeCapabilityCatalog catalog) {
        List<String> errors = new ArrayList<>();
        if (catalog == null) {
            errors.add("NODE_CATALOG_REQUIRED");
            return errors;
        }
        if (catalog.getSchemaVersion() != NodeCapabilityCatalog.CURRENT_SCHEMA_VERSION) {
            errors.add("NODE_CATALOG_SCHEMA_VERSION_UNSUPPORTED");
        }

        Set<String> typeIds = new HashSet<>();
        List<NodeTypeDescriptor> descriptors = catalog.getNodeTypes();
        if (descriptors == null) {
            errors.add("NODE_CATALOG_NODE_TYPES_REQUIRED");
            return errors;
        }

        for (int i = 0; i < descriptors.size(); i++) {
            NodeTypeDescriptor descriptor = descriptors.get(i);
            String prefix = "nodeTypes[" + i + "].";
            if (descriptor == null) {
                errors.add(prefix + "NODE_TYPE_REQUIRED");
                continue;
            }
            String typeId = descriptor.getTypeId();
            if (isBlank(typeId)) {
                errors.add(prefix + "NODE_TYPE_ID_REQUIRED");
            } else if (!typeIds.add(typeId)) {
                errors.add(prefix + "NODE_TYPE_ID_DUPLICATE");
            }
            if (isBlank(descriptor.getVersion())) errors.add(prefix + "NODE_TYPE_VERSION_REQUIRED");
            if (isBlank(descriptor.getDisplayName())) errors.add(prefix + "NODE_TYPE_DISPLAY_NAME_REQUIRED");
            if (isBlank(descriptor.getCategory())) errors.add(prefix + "NODE_TYPE_CATEGORY_REQUIRED");

            validatePorts(descriptor.getInputs(), NodePortDirection.INPUT, prefix + "inputs.", errors);
            validatePorts(descriptor.getOutputs(), NodePortDirection.OUTPUT, prefix + "outputs.", errors);
            validateRequirements(descriptor.getRequiredCapabilities(), prefix + "requiredCapabilities.", errors);
            validateRequirements(descriptor.getRequiredPermissions(), prefix + "requiredPermissions.", errors);
        }
        return errors;
    }

    private void validatePorts(List<NodePortDescriptor> ports, NodePortDirection expected,
            String prefix, List<String> errors) {
        if (ports == null) return;
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < ports.size(); i++) {
            NodePortDescriptor port = ports.get(i);
            String path = prefix + "[" + i + "].";
            if (port == null) {
                errors.add(path + "NODE_PORT_REQUIRED");
                continue;
            }
            if (isBlank(port.getId())) errors.add(path + "NODE_PORT_ID_REQUIRED");
            else if (!ids.add(port.getId())) errors.add(path + "NODE_PORT_ID_DUPLICATE");
            if (port.getDirection() != expected) errors.add(path + "NODE_PORT_DIRECTION_INVALID");
            if (isBlank(port.getDataType())) errors.add(path + "NODE_PORT_DATA_TYPE_REQUIRED");
        }
    }

    private void validateRequirements(List<String> values, String prefix, List<String> errors) {
        if (values == null) return;
        Set<String> unique = new HashSet<>();
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            if (isBlank(value)) errors.add(prefix + "[" + i + "].REQUIRED_VALUE_EMPTY");
            else if (!unique.add(value)) errors.add(prefix + "[" + i + "].REQUIRED_VALUE_DUPLICATE");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
