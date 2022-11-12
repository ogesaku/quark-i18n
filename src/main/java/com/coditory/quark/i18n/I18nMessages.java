package com.coditory.quark.i18n;

import com.coditory.quark.i18n.api.I18nKey;
import com.coditory.quark.i18n.api.I18nKeyGenerator;
import com.coditory.quark.i18n.api.I18nPath;
import com.coditory.quark.i18n.api.I18nUnresolvedMessageHandler;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class I18nMessages {
    private final Map<I18nKey, MessageTemplate> templates;
    private final I18nKeyGenerator keyGenerator;
    private final I18nUnresolvedMessageHandler unresolvedMessageHandler;
    private final MessageTemplateFormatter templateFormatter;
    private final Locale locale;
    private final List<I18nPath> prefixes;

    public I18nMessages(Map<I18nKey, MessageTemplate> templates, I18nKeyGenerator keyGenerator, I18nUnresolvedMessageHandler unresolvedMessageHandler, MessageTemplateFormatter templateFormatter, Locale locale, List<I18nPath> prefixes) {
        this.templates = templates;
        this.keyGenerator = keyGenerator;
        this.unresolvedMessageHandler = unresolvedMessageHandler;
        this.templateFormatter = templateFormatter;
        this.locale = locale;
        this.prefixes = prefixes;
    }

    public String getMessage(String key, Object... args) {
        I18nKey messageKey = I18nKey.of(locale, key);
        return keyGenerator.keys(prefixes, messageKey)
                .stream()
                .map(templates::get)
                .filter(Objects::nonNull)
                .map(message -> message.format(args))
                .findFirst()
                .orElseGet(() -> unresolvedMessageHandler.onUnresolvedMessage(messageKey, args));
    }

    public I18nMessages withLocale(Locale locale) {
        return new I18nMessages(
                templates, keyGenerator, unresolvedMessageHandler, templateFormatter, locale, prefixes);
    }

    public I18nMessages withPrefix(String prefix) {
        List<I18nPath> prefixes = List.of(I18nPath.of(prefix));
        return new I18nMessages(
                templates, keyGenerator, unresolvedMessageHandler, templateFormatter, locale, prefixes);
    }

    public String getMessage(String key) {
        return getMessage(key, new Object[0]);
    }

    public String format(String template, Object... args) {
        return templateFormatter.format(locale, template, args);
    }

    public String format(String message) {
        return format(message, new Object[0]);
    }
}
