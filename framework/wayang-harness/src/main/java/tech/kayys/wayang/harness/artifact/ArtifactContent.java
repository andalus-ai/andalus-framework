package tech.kayys.wayang.harness.artifact;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Representation of the raw data content of an artifact.
 */
public record ArtifactContent(byte[] bytes, String mimeType) {
    public ArtifactContent {
        Objects.requireNonNull(bytes, "bytes");
        mimeType = mimeType != null ? mimeType : "application/octet-stream";
    }

    public static ArtifactContent ofBytes(byte[] bytes, String mimeType) {
        return new ArtifactContent(bytes, mimeType);
    }

    public static ArtifactContent ofText(String text) {
        Objects.requireNonNull(text, "text");
        return new ArtifactContent(text.getBytes(StandardCharsets.UTF_8), "text/plain; charset=UTF-8");
    }

    public String asText() {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public InputStream openStream() {
        return new ByteArrayInputStream(bytes);
    }

    public long size() {
        return bytes.length;
    }
}
