package tech.kayys.andalus.resource;
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
 * Artifact Types
 */
public enum ArtifactType implements ResourceType {
    TEXT,
    JSON,
    XML,
    YAML,
    HTML,
    MARKDOWN,
    IMAGE,
    AUDIO,
    VIDEO,
    PDF,
    WORD,
    EXCEL,
    CSV,
    SQL,
    CODE,
    BINARY,
    EMBEDDING,
    PLAN,
    REASONING,
    CHART,
    REPORT
}
