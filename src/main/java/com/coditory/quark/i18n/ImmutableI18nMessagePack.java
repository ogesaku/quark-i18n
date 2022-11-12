package com.coditory.quark.i18n;

import com.coditory.quark.i18n.api.I18nKey;
import com.coditory.quark.i18n.api.I18nKeyGenerator;
import com.coditory.quark.i18n.api.I18nMessagePack;
import com.coditory.quark.i18n.api.I18nUnresolvedMessageHandler;

import java.util.List;
import java.util.Locale;
import java.util.Map;

class ImmutableI18nMessagePack implements I18nMessagePack {
    private final Map<I18nKey, MessageTemplate> templates;
    private final MessageTemplateFormatter templateFormatter;
    private final I18nUnresolvedMessageHandler unresolvedMessageHandler;
    private final I18nKeyGenerator keyGenerator;

    public ImmutableI18nMessagePack(Map<I18nKey, MessageTemplate> templates, MessageTemplateFormatter templateFormatter, I18nUnresolvedMessageHandler unresolvedMessageHandler, I18nKeyGenerator keyGenerator) {
        this.templates = templates;
        this.templateFormatter = templateFormatter;
        this.unresolvedMessageHandler = unresolvedMessageHandler;
        this.keyGenerator = keyGenerator;
    }

    @Override
    public I18nMessages forLocale(Locale locale) {
        return new I18nMessages(
                templates,
                keyGenerator,
                unresolvedMessageHandler,
                templateFormatter,
                locale,
                List.of()
        );
    }

    @Override
    public String format(Locale locale, String template, Object... args) {
        return templateFormatter.format(locale, template, args);
    }
}
