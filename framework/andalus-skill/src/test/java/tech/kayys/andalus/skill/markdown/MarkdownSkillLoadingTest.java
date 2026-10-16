package tech.kayys.andalus.skill.markdown;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tech.kayys.andalus.skill.spi.SkillKind;
import tech.kayys.andalus.skill.spi.SkillManifest;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownSkillLoadingTest {

    @Test
    void frontMatterParserExtractsAttributesAndBody() {
        String markdown = """
                ---
                id: test-skill
                name: Test Skill
                version: 1.2.3
                kind: INSTRUCTION
                category: tools
                capabilities:
                  - web.search
                  - web.fetch
                ---
                
                # Instruction Body
                
                Execute this skill carefully.
                """;

        MarkdownSkillDocument doc = MarkdownFrontMatterParser.parse(markdown);

        assertThat(doc.frontMatter()).containsEntry("id", "test-skill");
        assertThat(doc.frontMatter()).containsEntry("name", "Test Skill");
        assertThat(doc.frontMatter()).containsEntry("version", "1.2.3");
        assertThat(doc.frontMatter()).containsEntry("kind", "INSTRUCTION");
        assertThat(doc.frontMatter()).containsEntry("category", "tools");
        assertThat(doc.body()).contains("# Instruction Body");
        assertThat(doc.body()).contains("Execute this skill carefully.");
    }

    @Test
    void packageParserCreatesSkillPackage() {
        String markdown = """
                ---
                id: web-research
                name: Web Research
                version: 1.0.0
                kind: INSTRUCTION
                description: Researches web topics
                capabilities:
                  - web.search
                ---
                
                ## Steps
                1. Search.
                2. Summarize.
                """;

        DefaultSkillPackageParser parser = new DefaultSkillPackageParser();
        SkillPackage pkg = parser.parse(markdown);

        assertThat(pkg.manifest().id()).isEqualTo("web-research");
        assertThat(pkg.manifest().name()).isEqualTo("Web Research");
        assertThat(pkg.manifest().version()).isEqualTo("1.0.0");
        assertThat(pkg.manifest().kind()).isEqualTo(SkillKind.INSTRUCTION);
        assertThat(pkg.manifest().capabilities()).containsExactly("web.search");

        assertThat(pkg.content().content()).contains("## Steps");
        assertThat(pkg.content().metadata().id()).isEqualTo("web-research");
    }

    @Test
    void standaloneManifestTakesPrecedenceOverFrontMatter() {
        String markdown = """
                ---
                id: fallback-id
                name: Fallback Name
                version: 0.1.0
                kind: INSTRUCTION
                ---
                Content here
                """;

        String manifestYaml = """
                ---
                id: authoritative-id
                name: Authoritative Name
                version: 2.0.0
                kind: COMPOSITE
                category: advanced
                ---
                """;

        DefaultSkillPackageParser parser = new DefaultSkillPackageParser();
        SkillPackage pkg = parser.parse(markdown, manifestYaml);

        assertThat(pkg.manifest().id()).isEqualTo("authoritative-id");
        assertThat(pkg.manifest().name()).isEqualTo("Authoritative Name");
        assertThat(pkg.manifest().version()).isEqualTo("2.0.0");
        assertThat(pkg.manifest().kind()).isEqualTo(SkillKind.COMPOSITE);
        assertThat(pkg.content().content()).contains("Content here");
    }

    @Test
    void packageLoaderLoadsFromDirectory(@TempDir Path tempDir) throws Exception {
        Path skillMd = tempDir.resolve("SKILL.md");
        Files.writeString(skillMd, """
                ---
                id: fs-skill
                name: Filesystem Skill
                version: 1.0.0
                kind: INSTRUCTION
                ---
                Body from disk.
                """);

        DefaultSkillPackageLoader loader = new DefaultSkillPackageLoader();
        SkillPackage pkg = loader.load(tempDir).toCompletableFuture().get();

        assertThat(pkg.manifest().id()).isEqualTo("fs-skill");
        assertThat(pkg.manifest().name()).isEqualTo("Filesystem Skill");
        assertThat(pkg.content().content()).contains("Body from disk.");
    }

    @Test
    void packageLoaderLoadsDirectFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("custom.md");
        Files.writeString(file, """
                ---
                id: direct-file-skill
                name: Direct File Skill
                version: 3.0.0
                kind: INSTRUCTION
                ---
                Direct file content.
                """);

        DefaultSkillPackageLoader loader = new DefaultSkillPackageLoader();
        SkillPackage pkg = loader.load(file).toCompletableFuture().get();

        assertThat(pkg.manifest().id()).isEqualTo("direct-file-skill");
        assertThat(pkg.content().content()).contains("Direct file content.");
    }
}
