package com.coditory.quark.i18n;

import com.coditory.quark.i18n.loader.I18nTemplates;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PrimitiveIterator;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;
import static java.util.Collections.unmodifiableMap;

final class ReferenceResolver {
    private final Map<I18nKey, I18nTemplates> bundles;
    private final Map<I18nKey, String> templates;
    private final I18nKeyGenerator keyGenerator;

    ReferenceResolver(List<I18nTemplates> entries, I18nKeyGenerator keyGenerator) {
        Map<I18nKey, String> templates = new HashMap<>();
        Map<I18nKey, I18nTemplates> bundles = new HashMap<>();
        for (I18nTemplates templateEntry : entries) {
            templates.putAll(templateEntry.templates());
            templateEntry.templates().keySet()
                    .forEach(key -> bundles.put(key, templateEntry));
        }
        this.keyGenerator = expectNonNull(keyGenerator, "keyGenerator");
        this.templates = unmodifiableMap(templates);
        this.bundles = unmodifiableMap(bundles);
    }

    String resolveReferences(I18nKey key, String template) {
        return resolveReferences(key.locale(), key.path(), template, 0);
    }

    String resolveReferences(Locale locale, String template) {
        return resolveReferences(locale, null, template, 0);
    }

    private String resolveReferences(Locale locale, I18nPath path, String template, int iteration) {
        if (!template.contains("$")) {
            return template;
        }
        if (iteration + 1 > 10) {
            throw new IllegalArgumentException("Detected potential cycle in message references");
        }
        StringBuilder result = new StringBuilder();
        StringBuilder reference = new StringBuilder();
        boolean modified = false;
        boolean dollar = false;
        boolean brace = false;
        boolean escaped = false;
        for (PrimitiveIterator.OfInt it = template.codePoints().iterator(); it.hasNext(); ) {
            int c = it.next();
            if (c == '\\') {
                escaped = true;
            } else if (escaped) {
                if (c != '$') {
                    result.append('\\');
                } else {
                    modified = true;
                }
                result.append(c);
                escaped = false;
            } else if (c == '$') {
                modified = true;
                dollar = true;
            } else if (c == '{' && brace) {
                throw new IllegalArgumentException("Duplicated '{'");
            } else if (c == '{' && dollar && !reference.isEmpty()) {
                throw new IllegalArgumentException("Unexpected '{'");
            } else if (c == '{' && dollar) {
                brace = true;
            } else if ((c == '}' && brace) || (c == ' ' && dollar)) {
                I18nPath referencePath = I18nPath.of(reference.toString());
                String referenceTemplate = resolveReference(locale, path, referencePath);
                result.append(referenceTemplate);
                reference = new StringBuilder();
                dollar = false;
                brace = false;
            } else if (dollar) {
                if (!Character.isWhitespace(c)) {
                    reference.append(c);
                }
            } else {
                result.appendCodePoint(c);
            }
        }
        if (escaped) {
            result.append('\\');
        }
        return modified
                ? resolveReferences(locale, path, result.toString(), iteration + 1)
                : template;
    }

    private String resolveReference(Locale locale, I18nPath sourcePath, I18nPath referencePath) {
        I18nKey referenceKey = I18nKey.of(locale, referencePath);
        I18nTemplates bundle = sourcePath != null
                ? bundles.get(I18nKey.of(locale, sourcePath))
                : null;
        List<I18nKey> keys = bundle != null && bundle.prefix() != null
                ? keyGenerator.keys(referenceKey, bundle.prefix())
                : keyGenerator.keys(referenceKey);
        return keys.stream()
                .map(templates::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Reference not found: " + referencePath));
    }
}
