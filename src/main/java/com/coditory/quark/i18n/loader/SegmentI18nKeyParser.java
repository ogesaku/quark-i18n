package com.coditory.quark.i18n.loader;

import com.coditory.quark.i18n.I18nKey;
import com.coditory.quark.i18n.I18nPath;
import com.coditory.quark.i18n.Locales;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

final class SegmentI18nKeyParser implements I18nKeyParser {
    @Override
    public Map<I18nKey, String> parseKeys(PathTemplate pathTemplate, File file, Map<String, Object> values) {
        String prefix = pathTemplate.extractPrefix(file.getAbsolutePath());
        I18nPath parentPath = prefix != null ? I18nPath.of(prefix) : I18nPath.root();
        return parseKeys(parentPath, values);
    }

    public Map<I18nKey, String> parseKeys(I18nPath parent, Map<String, Object> values) {
        Map<I18nKey, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (entry.getValue() instanceof Map<?, ?>) {
                I18nPath path = parent.child(entry.getKey());
                result.putAll(parseKeys(path, values));
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
