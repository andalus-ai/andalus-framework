package tech.kayys.wayang.execution.filesystem;

/**
 * Operations on files and directories within a sandbox.
 */
public enum FilesystemOperation {
    READ,
    WRITE,
    CREATE,
    DELETE,
    EXECUTE,
    MOUNT
}
