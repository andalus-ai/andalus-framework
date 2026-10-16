package tech.kayys.andalus.skill.markdown;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Default filesystem-based implementation of {@link SkillPackageLoader}.
 *
 * <p>Supports:
 * <ul>
 *   <li>Directory containing <code>SKILL.md</code> (and optional <code>manifest.yaml</code>)</li>
 *   <li>Direct path to a Markdown file (e.g. <code>/path/to/my-skill.md</code>)</li>
 * </ul>
 */
public final class DefaultSkillPackageLoader implements SkillPackageLoader {

    private final SkillPackageParser parser;
    private final Executor executor;

    public DefaultSkillPackageLoader() {
        this(new DefaultSkillPackageParser(), ForkJoinPool.commonPool());
    }

    public DefaultSkillPackageLoader(SkillPackageParser parser, Executor executor) {
        this.parser   = Objects.requireNonNull(parser, "parser must not be null");
        this.executor = Objects.requireNonNull(executor, "executor must not be null");
    }

    @Override
    public CompletionStage<SkillPackage> load(Path path) {
        Objects.requireNonNull(path, "path must not be null");

        return CompletableFuture.supplyAsync(() -> {
            try {
                if (Files.isDirectory(path)) {
                    Path skillMd = path.resolve("SKILL.md");
                    if (!Files.exists(skillMd)) {
                        skillMd = path.resolve("skill.md");
                    }
                    if (!Files.exists(skillMd)) {
                        throw new IllegalArgumentException("Directory does not contain SKILL.md: " + path);
                    }

                    String markdown = Files.readString(skillMd);

                    String manifestText = null;
                    Path manifestFile = path.resolve("manifest.yaml");
                    if (!Files.exists(manifestFile)) {
                        manifestFile = path.resolve("manifest.yml");
                    }
                    if (Files.exists(manifestFile)) {
                        manifestText = Files.readString(manifestFile);
                    }

                    return parser.parse(markdown, manifestText);
                } else if (Files.isRegularFile(path)) {
                    String markdown = Files.readString(path);
                    return parser.parse(markdown, null);
                } else {
                    throw new IllegalArgumentException("Path is neither a regular file nor directory: " + path);
                }
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load skill package from " + path, e);
            }
        }, executor);
    }
}
