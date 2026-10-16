package tech.kayys.andalus.skill.markdown;

import tech.kayys.andalus.skill.spi.DefaultSkillManifestParser;
import tech.kayys.andalus.skill.spi.SkillContent;
import tech.kayys.andalus.skill.spi.SkillManifest;
import tech.kayys.andalus.skill.spi.SkillManifestParser;
import tech.kayys.andalus.skill.spi.SkillMetadata;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default implementation of {@link SkillPackageParser}.
 *
 * <p>Supports precedence rule from Phase 2L.3:</p>
 * <pre>
 * manifest.yaml (authoritative)
 *   ↓ overrides
 * SKILL.md front matter (convenience/fallback)
 *   ↓
 * Markdown body (instructions)
 * </pre>
 */
public final class DefaultSkillPackageParser implements SkillPackageParser {

    private final SkillManifestParser manifestParser;

    public DefaultSkillPackageParser() {
        this(new DefaultSkillManifestParser());
    }

    public DefaultSkillPackageParser(SkillManifestParser manifestParser) {
        this.manifestParser = Objects.requireNonNull(manifestParser, "manifestParser must not be null");
    }

    @Override
    public SkillPackage parse(String markdown, String manifestText) {
        MarkdownSkillDocument doc = MarkdownFrontMatterParser.parse(markdown);

        Map<String, Object> combinedManifestMap = new LinkedHashMap<>(doc.frontMatter());

        if (manifestText != null && !manifestText.isBlank()) {
            MarkdownSkillDocument manifestDoc = MarkdownFrontMatterParser.parse(manifestText);
            combinedManifestMap.putAll(manifestDoc.frontMatter());
        }

        SkillManifest manifest = manifestParser.parse(combinedManifestMap);

        SkillMetadata metadata = SkillMetadata.builder()
                .id(manifest.id())
                .name(manifest.name())
                .version(manifest.version())
                .description(manifest.description())
                .category(manifest.category())
                .tags(List.copyOf(manifest.capabilities()))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .enabled(true)
                .build();

        SkillContent content = SkillContent.builder()
                .metadata(metadata)
                .content(doc.body())
                .manifest(combinedManifestMap)
                .build();

        return new SkillPackage(manifest, content);
    }
}
