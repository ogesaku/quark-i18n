package com.coditory.quark.i18n;

import com.coditory.quark.i18n.formatter.DateI18NFormatterProvider;
import com.coditory.quark.i18n.formatter.DateTimeI18NFormatterProvider;
import com.coditory.quark.i18n.formatter.I18nFormatterProvider;
import com.coditory.quark.i18n.formatter.MoneyI18NFormatterProvider;
import com.coditory.quark.i18n.formatter.NumberI18NFormatterProvider;
import com.coditory.quark.i18n.formatter.PluralI18NFormatterProvider;
import com.coditory.quark.i18n.formatter.TimeI18NFormatterProvider;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coditory.quark.i18n.I18nKeyGenerator.relaxedI18nKeyGenerator;
import static com.coditory.quark.i18n.Preconditions.expectNonBlank;
import static com.coditory.quark.i18n.Preconditions.expectNonNull;
import static java.util.Map.entry;
import static java.util.stream.Collectors.toMap;

public final class I18nMessagePackBuilder {
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
    private final List<I18nPath> prefixes = new ArrayList<>();
    private I18nKeyGenerator keyGenerator = relaxedI18nKeyGenerator();
    private I18nUnresolvedMessageHandler unresolvedMessageHandler = I18nUnresolvedMessageHandler.throwError();

    @NotNull
    public I18nMessagePackBuilder addMessage(@NotNull Locale locale, @NotNull String key, @NotNull String message) {
        expectNonNull(locale, "locale");
        expectNonBlank(key, "key");
        expectNonBlank(message, "message");
        messagesBuilder.addMessage(locale, key, message);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder addFormatter(@NotNull Class<?> type, @NotNull I18nFormatterProvider formatter) {
        expectNonNull(type, "type");
        expectNonNull(formatter, "formatter");
        typeFormatters.put(type, formatter);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder addFormatter(@NotNull String name, @NotNull I18nFormatterProvider formatter) {
        expectNonBlank(name, "name");
        expectNonNull(formatter, "formatter");
        namedFormatters.put(name, formatter);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder setUnresolvedMessageHandler(@NotNull I18nUnresolvedMessageHandler unresolvedMessageHandler) {
        expectNonNull(unresolvedMessageHandler, "unresolvedMessageHandler");
        this.unresolvedMessageHandler = unresolvedMessageHandler;
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder setKeyGenerator(@NotNull I18nKeyGenerator keyGenerator) {
        expectNonNull(keyGenerator, "keyGenerator");
        this.keyGenerator = keyGenerator;
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder setDefaultLocale(@NotNull Locale defaultLocale) {
        expectNonNull(defaultLocale, "defaultLocale");
        this.keyGenerator = relaxedI18nKeyGenerator(defaultLocale);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder setPrefixes(@NotNull List<String> prefixes) {
        expectNonNull(prefixes, "prefixes");
        this.prefixes.clear();
        prefixes.forEach(this::addPrefix);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder setPrefixes(@NotNull String... prefixes) {
        expectNonNull(prefixes, "prefixes");
        this.prefixes.clear();
        Arrays.stream(prefixes).forEach(this::addPrefix);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder addPrefix(@NotNull String prefix) {
        expectNonBlank(prefix, "prefix");
        I18nPath.validate(prefix);
        I18nPath path = I18nPath.of(prefix);
        this.prefixes.add(path);
        return this;
    }

    @NotNull
    public I18nMessagePack build() {
        I18nMessageTemplatesPack messages = messagesBuilder
                .withI18nKeyGenerator(keyGenerator)
                .build();
        MessageTemplateParser parser = new MessageTemplateParser(messages, this.namedFormatters, this.typeFormatters);
        Map<I18nKey, MessageTemplate> templates = parseTemplates(messages, parser);
        return new ImmutableI18nMessagePack(templates, parser, unresolvedMessageHandler, keyGenerator, prefixes);
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
}
