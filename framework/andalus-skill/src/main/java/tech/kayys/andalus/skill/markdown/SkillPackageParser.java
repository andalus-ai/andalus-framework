package tech.kayys.andalus.skill.markdown;

/**
 * Parses raw Markdown text and optional manifest text into a structured {@link SkillPackage}.
 */
public interface SkillPackageParser {

    /**
     * Parse markdown content and optional standalone manifest.
     *
     * @param markdown the markdown content (typically with YAML front-matter)
     * @param manifest optional YAML manifest string; may be null or empty
     * @return the parsed SkillPackage
     */
    SkillPackage parse(String markdown, String manifest);

    /**
     * Parse markdown content whose front-matter contains the full manifest.
     *
     * @param markdown the markdown content with YAML front-matter
     * @return the parsed SkillPackage
     */
    default SkillPackage parse(String markdown) {
        return parse(markdown, null);
    }
}
