package tech.kayys.andalus.skill.markdown;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser that splits a Markdown file into front-matter key/value attributes
 * and instructional Markdown body.
 */
public final class MarkdownFrontMatterParser {

    private MarkdownFrontMatterParser() {}

    public static MarkdownSkillDocument parse(String text) {
        if (text == null || text.isBlank()) {
            return new MarkdownSkillDocument(Map.of(), "");
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
            String firstLine = reader.readLine();
            if (firstLine == null || !firstLine.trim().equals("---")) {
                return new MarkdownSkillDocument(Map.of(), text);
            }

            Map<String, Object> frontMatter = new LinkedHashMap<>();
            StringBuilder bodyBuilder = new StringBuilder();
            boolean inFrontMatter = true;
            String line;
            String currentListKey = null;
            List<String> currentList = null;

            while ((line = reader.readLine()) != null) {
                if (inFrontMatter) {
                    if (line.trim().equals("---")) {
                        inFrontMatter = false;
                        if (currentListKey != null && currentList != null) {
                            frontMatter.put(currentListKey, currentList);
                            currentListKey = null;
                            currentList = null;
                        }
                        continue;
                    }

                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                        continue;
                    }

                    if (trimmed.startsWith("- ") && currentListKey != null) {
                        currentList.add(trimmed.substring(2).trim());
                    } else {
                        if (currentListKey != null && currentList != null) {
                            frontMatter.put(currentListKey, currentList);
                            currentListKey = null;
                            currentList = null;
                        }

                        int colonIdx = line.indexOf(':');
                        if (colonIdx > 0) {
                            String key = line.substring(0, colonIdx).trim();
                            String val = line.substring(colonIdx + 1).trim();

                            if (val.isEmpty()) {
                                currentListKey = key;
                                currentList = new ArrayList<>();
                            } else {
                                frontMatter.put(key, unquote(val));
                            }
                        }
                    }
                } else {
                    bodyBuilder.append(line).append("\n");
                }
            }

            return new MarkdownSkillDocument(frontMatter, bodyBuilder.toString().trim());
        } catch (Exception e) {
            return new MarkdownSkillDocument(Map.of(), text);
        }
    }

    private static String unquote(String val) {
        if (val.startsWith("\"") && val.endsWith("\"") && val.length() >= 2) {
            return val.substring(1, val.length() - 1);
        }
        if (val.startsWith("'") && val.endsWith("'") && val.length() >= 2) {
            return val.substring(1, val.length() - 1);
        }
        return val;
    }
}
