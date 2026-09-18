package tech.kayys.wayang.artifact;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.wayang.extension.Extension;
import tech.kayys.wayang.resource.Resource;
import tech.kayys.wayang.resource.BaseResource;



/**
 * Artifact Formats
 */
public enum ArtifactFormat {
    /** Plain text content. */
    TEXT,
    /** JSON content. */
    JSON,
    /** XML content. */
    XML,
    /** YAML content. */
    YAML,
    /** HTML content. */
    HTML,
    /** Markdown content. */
    MARKDOWN,
    /** PNG image content. */
    PNG,
    /** JPEG image content. */
    JPEG,
    /** GIF image content. */
    GIF,
    /** WebP image content. */
    WEBP,
    /** MP3 audio content. */
    MP3,
    /** WAV audio content. */
    WAV,
    /** MP4 video content. */
    MP4,
    /** WebM video content. */
    WEBM,
    /** PDF document content. */
    PDF,
    /** DOCX document content. */
    DOCX,
    /** XLSX spreadsheet content. */
    XLSX,
    /** CSV tabular content. */
    CSV,
    /** SQL source content. */
    SQL,
    /** Java source content. */
    JAVA,
    /** Python source content. */
    PYTHON,
    /** JavaScript source content. */
    JAVASCRIPT,
    /** TypeScript source content. */
    TYPESCRIPT,
    /** Binary content with no more specific format. */
    BINARY,
    /** Streaming content. */
    STREAM,
    /** Unknown or unrecognized content. */
    UNKNOWN
}
