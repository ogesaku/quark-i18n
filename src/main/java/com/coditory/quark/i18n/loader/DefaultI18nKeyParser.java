package com.coditory.quark.i18n.loader;

import com.coditory.quark.i18n.I18nKey;
import com.coditory.quark.i18n.I18nPath;
import com.coditory.quark.i18n.Locales;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

final class DefaultI18nKeyParser implements I18nKeyParser {
    private static final I18nKeyParser FILE_NAME_PARSER = new FileNameI18nKeyParser();
    private static final I18nKeyParser SEGMENT_PARSER = new SegmentI18nKeyParser();

    @Override
    public Map<I18nKey, String> parseKeys(I18nPathPattern pathPattern, File file, Map<String, Object> parsed) {
        return pathPattern.hasLangGroup()
                ? FILE_NAME_PARSER.parseKeys(pathPattern, file, parsed)
                : SEGMENT_PARSER.parseKeys(pathPattern, file, parsed);
    }

    private static final class FileNameI18nKeyParser implements I18nKeyParser {
        @Override
        public Map<I18nKey, String> parseKeys(I18nPathPattern pathPattern, File file, Map<String, Object> values) {
            I18nPathPattern.I18nPathGroups matchedSegments = pathPattern.matchGroups(file);
            I18nPath parentPath = matchedSegments.path() != null
                    ? matchedSegments.path()
                    : I18nPath.root();
            Locale locale = matchedSegments.locale();
            if (locale == null) {
                throw new I18nParseException("Could not pick locale for file: " + file.getAbsolutePath());
            }
            I18nKey parentKey = I18nKey.of(locale, parentPath);
            return parseKeys(parentKey, values);
        }

        @SuppressWarnings("unchecked")
        public Map<I18nKey, String> parseKeys(I18nKey parent, Map<String, Object> values) {
            Map<I18nKey, String> result = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                I18nKey key = parent.child(entry.getKey());
                if (entry.getValue() instanceof Map<?, ?>) {
                    Map<String, Object> children = (Map<String, Object>) entry.getValue();
                    result.putAll(parseKeys(key, children));
                } else if (entry.getValue() instanceof Collection<?>) {
                    throw new I18nParseException("Could not parse value for: " + key);
                } else {
                    String value = entry.getValue().toString();
                    result.put(key, value);
                }
            }
            return result;
        }
    }

    private static final class SegmentI18nKeyParser implements I18nKeyParser {
        @Override
        public Map<I18nKey, String> parseKeys(I18nPathPattern pathPattern, File file, Map<String, Object> values) {
            I18nPath path = pathPattern.matchGroups(file).path();
            I18nPath parentPath = path != null ? path : I18nPath.root();
            return parseKeys(parentPath, values);
        }

        @SuppressWarnings("unchecked")
        public Map<I18nKey, String> parseKeys(I18nPath parent, Map<String, Object> values) {
            Map<I18nKey, String> result = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                if (entry.getValue() instanceof Map<?, ?>) {
                    Map<String, Object> children = (Map<String, Object>) entry.getValue();
                    I18nPath path = parent.child(entry.getKey());
                    result.putAll(parseKeys(path, children));
                } else if (entry.getValue() instanceof Collection<?>) {
                    I18nPath path = parent.child(entry.getKey());
                    throw new I18nParseException("Could not parse value for: " + path);
                } else {
                    Locale locale = Locales.parseLocale(entry.getKey());
                    String value = entry.getValue().toString();
                    I18nKey key = I18nKey.of(locale, parent);
                    result.put(key, value);
                }
            }
            return result;
        }
    }
}
