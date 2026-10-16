package tech.kayys.andalus.skill.markdown;

import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

/**
 * Loads a {@link SkillPackage} from a filesystem directory or markdown file.
 */
public interface SkillPackageLoader {

    /**
     * Asynchronously load a SkillPackage from the given path.
     *
     * @param path path to a package directory or a .md file
     * @return completion stage with the loaded SkillPackage
     */
    CompletionStage<SkillPackage> load(Path path);
}
