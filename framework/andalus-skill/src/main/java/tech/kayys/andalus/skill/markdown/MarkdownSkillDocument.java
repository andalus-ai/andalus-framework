package tech.kayys.andalus.skill.markdown;

import java.util.Map;

/**
 * Parsed representation of a Markdown document with YAML-like front-matter.
 */
public record MarkdownSkillDocument(
        Map<String, Object> frontMatter,
        String body
) {

    public MarkdownSkillDocument {
        frontMatter = frontMatter == null ? Map.of() : Map.copyOf(frontMatter);
        body        = body        == null ? ""       : body;
    }
}
