package com.coditory.quark.i18n;

final class MessageTemplateNormalizer {
    private final boolean normalizeWhiteSpaces;

    MessageTemplateNormalizer(boolean normalizeWhiteSpaces) {
        this.normalizeWhiteSpaces = normalizeWhiteSpaces;
    }

    String normalize(String template) {
        if (!normalizeWhiteSpaces) {
            return template;
        }
        return template.trim()
                .replaceAll("\\s+", " ");
    }
}
