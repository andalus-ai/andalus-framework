package tech.kayys.wayang.harness.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Execution-facing handle to perform sandboxed file operations within a workspace.
 */
public interface WorkspaceHandle {

    WorkspacePath root();

    WorkspacePath resolve(String relativePath);

    InputStream read(WorkspacePath path) throws IOException;

    OutputStream write(WorkspacePath path) throws IOException;

    boolean exists(WorkspacePath path);

    List<WorkspacePath> list(WorkspacePath path) throws IOException;

    boolean delete(WorkspacePath path) throws IOException;
}
