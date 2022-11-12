package com.coditory.quark.i18n;

import com.coditory.quark.i18n.api.I18nKey;
import com.coditory.quark.i18n.api.I18nKeyGenerator;
import com.coditory.quark.i18n.api.I18nMessagePack;
import com.coditory.quark.i18n.api.I18nUnresolvedMessageHandler;
import com.coditory.quark.i18n.api.ReloadableI18nMessagePack;
import com.coditory.quark.i18n.formatter.DateI18NFormatterProvider;
import com.coditory.quark.i18n.formatter.DateTimeI18NFormatterProvider;
import com.coditory.quark.i18n.formatter.I18nFormatterProvider;
import com.coditory.quark.i18n.formatter.MoneyI18NFormatterProvider;
import com.coditory.quark.i18n.formatter.NumberI18NFormatterProvider;
import com.coditory.quark.i18n.formatter.PluralI18NFormatterProvider;
import com.coditory.quark.i18n.formatter.TimeI18NFormatterProvider;

import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.util.Map.entry;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static com.coditory.quark.i18n.api.I18nKeyGenerator.relaxedI18nKeyGenerator;

public class I18nMessagePackBuilder {
    private final Map<Class<?>, I18nFormatterProvider> DEFAULT_TYPE_FORMATTERS = Map.of(
            Instant.class, new DateTimeI18NFormatterProvider(),
            Number.class, new NumberI18NFormatterProvider()
    );
    private final Map<String, I18nFormatterProvider> DEFAULT_NAMED_FORMATTERS = Map.of(
            "number", new NumberI18NFormatterProvider(),
            "money", new MoneyI18NFormatterProvider(),
            "dateTime", new DateTimeI18NFormatterProvider(),
            "date", new DateI18NFormatterProvider(),
            "time", new TimeI18NFormatterProvider(),
            "plural", new PluralI18NFormatterProvider()
    );
    private final I18nMessageTemplatesPack.I18nRawMessagesBuilder messagesBuilder = I18nMessageTemplatesPack.builder();
    private final Map<Class<?>, I18nFormatterProvider> typeFormatters = new HashMap<>(DEFAULT_TYPE_FORMATTERS);
    private final Map<String, I18nFormatterProvider> namedFormatters = new HashMap<>(DEFAULT_NAMED_FORMATTERS);
    private I18nKeyGenerator keyGenerator = relaxedI18nKeyGenerator();
    private I18nUnresolvedMessageHandler unresolvedMessageHandler = I18nUnresolvedMessageHandler.throwError();

    public I18nMessagePackBuilder addMessage(Locale locale, String key, String message) {
        messagesBuilder.addMessage(locale, key, message);
        return this;
    }

    public I18nMessagePackBuilder addFormatter(Class<?> type, I18nFormatterProvider formatter) {
        requireNonNull(type);
        requireNonNull(formatter);
        typeFormatters.put(type, formatter);
        return this;
    }

    public I18nMessagePackBuilder addFormatter(String name, I18nFormatterProvider formatter) {
        requireNonNull(name);
        requireNonNull(formatter);
        namedFormatters.put(name, formatter);
        return this;
    }

    public I18nMessagePackBuilder withUnresolvedMessageHandler(I18nUnresolvedMessageHandler unresolvedMessageHandler) {
        this.unresolvedMessageHandler = requireNonNull(unresolvedMessageHandler);
        return this;
    }

    public I18nMessagePackBuilder withKeyGenerator(I18nKeyGenerator keyGenerator) {
        this.keyGenerator = requireNonNull(keyGenerator);
        return this;
    }

    public I18nMessagePackBuilder withDefaultLocale(Locale defaultLocale) {
        requireNonNull(defaultLocale);
        this.keyGenerator = relaxedI18nKeyGenerator(defaultLocale);
        return this;
    }

    public I18nMessagePack build() {
        I18nMessageTemplatesPack messages = messagesBuilder
                .withI18nKeyGenerator(keyGenerator)
                .build();
        MessageTemplateParser parser = new MessageTemplateParser(messages, this.namedFormatters, this.typeFormatters);
        Map<I18nKey, MessageTemplate> templates = parseTemplates(messages, parser);
        return new ImmutableI18nMessagePack(templates, parser, unresolvedMessageHandler, keyGenerator);
    }

    private Map<I18nKey, MessageTemplate> parseTemplates(I18nMessageTemplatesPack messages, MessageTemplateParser parser) {
        return messages.getLocales().stream()
                .flatMap(locale -> parseTemplates(parser, messages, locale).entrySet().stream())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<I18nKey, MessageTemplate> parseTemplates(MessageTemplateParser parser, I18nMessageTemplatesPack messages, Locale locale) {
        return messages.filterMessagesWith(locale).entrySet().stream()
                .map(e -> entry(e.getKey(), parser.parse(locale, e.getValue())))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public ReloadableI18nMessagePack buildReloadable() {
        return new ReloadableI18nMessagePack(build());
    }
}
