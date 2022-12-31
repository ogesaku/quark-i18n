package com.coditory.quark.i18n;

import com.coditory.quark.i18n.loader.I18nTemplates;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

final class MessageTemplateParser {
    private final ReferenceResolver referenceResolver;
    private final ArgumentResolver argumentResolver;

    public MessageTemplateParser(ReferenceResolver referenceResolver, ArgumentResolver argumentResolver) {
        this.referenceResolver = expectNonNull(referenceResolver, "referenceResolver");
        this.argumentResolver = expectNonNull(argumentResolver, "argumentResolver");
    }

    Map<I18nKey, MessageTemplate> parseTemplates(List<I18nTemplates> bundles) {
        Map<I18nKey, MessageTemplate> result = new HashMap<>();
        for (I18nTemplates bundle : bundles) {
            for (Map.Entry<I18nKey, String> entry : bundle.templates().entrySet()) {
                I18nKey key = entry.getKey();
                String value = entry.getValue();
                MessageTemplate template = parseTemplate(key, value);
                result.put(key, template);
            }
        }
        return result;
    }

    MessageTemplate parseTemplate(I18nKey key, String template) {
        template = template.trim();
        template = referenceResolver.resolveReferences(key, template);
        return MessageTemplate.parse(template, argumentResolver);
    }

    MessageTemplate parseTemplate(Locale locale, String template) {
        template = referenceResolver.resolveReferences(locale, template);
        return MessageTemplate.parse(template, argumentResolver);
    }
}
