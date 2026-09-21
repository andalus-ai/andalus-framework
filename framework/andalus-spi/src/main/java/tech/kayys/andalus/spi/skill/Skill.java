package tech.kayys.andalus.spi.skill;

import tech.kayys.andalus.extension.Extension;

/**
 * Skill represents a set of related tools and behaviors.
 * This is the base SPI for Andalus-Projects/Andalus-Skills.
 */
public interface Skill extends Extension {
    
    /**
     * Gets the unique identifier of the skill.
     */
    String getId();
    
    /**
     * Gets the context or instructions provided by this skill.
     * @return Markdown or text instructions.
     */
    String getInstructions();
    
    /**
     * Optional trigger conditions for this skill to be dynamically loaded.
     */
    default String getTriggerDescription() {
        return "";
    }
}
