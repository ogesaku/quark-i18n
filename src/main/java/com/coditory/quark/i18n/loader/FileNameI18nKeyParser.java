package com.coditory.quark.i18n.loader;

import com.coditory.quark.i18n.I18nKey;
import com.coditory.quark.i18n.I18nPath;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

final class FileNameI18nKeyParser implements I18nKeyParser {
    private final Locale defaultLocale;

    public FileNameI18nKeyParser() {
        this.defaultLocale = null;
    }

    public FileNameI18nKeyParser(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    public Map<I18nKey, String> parseKeys(PathTemplate pathTemplate, File file, Map<String, Object> values) {
        Locale locale = pathTemplate.extractLocale(file.getAbsolutePath());
        locale = locale == null ? defaultLocale : locale;
        if (locale == null) {
            throw new I18nParseException("Could not pick locale for file: " + file.getAbsolutePath());
        }
        String prefix = pathTemplate.extractPrefix(file.getAbsolutePath());
        I18nPath parentPath = prefix != null ? I18nPath.of(prefix) : I18nPath.root();
        I18nKey parentKey = I18nKey.of(locale, parentPath);
        return parseKeys(parentKey, values);
    }

    public Map<I18nKey, String> parseKeys(I18nKey parent, Map<String, Object> values) {
        Map<I18nKey, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            I18nKey key = parent.child(entry.getKey());
            if (entry.getValue() instanceof Map<?, ?>) {
                result.putAll(parseKeys(key, values));
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
