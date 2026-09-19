package tech.kayys.wayang.execution.filesystem;

/**
 * Evaluates filesystem operations against sandbox containment rules.
 */
public interface FilesystemPolicy {

    AccessDecision evaluate(FilesystemOperation operation, PathReference path);

    static FilesystemPolicy readWrite() {
        return (op, path) -> AccessDecision.allow("Full read-write access allowed");
    }

    static FilesystemPolicy readOnly() {
        return (op, path) -> {
            if (op == FilesystemOperation.READ) {
                return AccessDecision.allow("Read allowed");
            }
            return AccessDecision.deny("Modification denied in read-only mode: " + op);
        };
    }
}
