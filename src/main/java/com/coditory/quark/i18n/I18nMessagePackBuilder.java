package com.coditory.quark.i18n;

import com.coditory.quark.i18n.formatter.DateI18nFormatterProvider;
import com.coditory.quark.i18n.formatter.DateTimeI18nFormatterProvider;
import com.coditory.quark.i18n.formatter.I18nFormatterProvider;
import com.coditory.quark.i18n.formatter.MoneyI18nFormatterProvider;
import com.coditory.quark.i18n.formatter.NumberI18nFormatterProvider;
import com.coditory.quark.i18n.formatter.PluralI18nFormatterProvider;
import com.coditory.quark.i18n.formatter.TimeI18nFormatterProvider;
import com.coditory.quark.i18n.loader.I18nFileLoaderFactory;
import com.coditory.quark.i18n.loader.I18nLoader;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystem;
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
            Instant.class, new DateTimeI18nFormatterProvider(),
            Number.class, new NumberI18nFormatterProvider()
    );
    private final Map<String, I18nFormatterProvider> DEFAULT_NAMED_FORMATTERS = Map.of(
            "number", new NumberI18nFormatterProvider(),
            "money", new MoneyI18nFormatterProvider(),
            "dateTime", new DateTimeI18nFormatterProvider(),
            "date", new DateI18nFormatterProvider(),
            "time", new TimeI18nFormatterProvider(),
            "plural", new PluralI18nFormatterProvider()
    );
    private final Map<Class<?>, I18nFormatterProvider> typeFormatters = new HashMap<>(DEFAULT_TYPE_FORMATTERS);
    private final Map<String, I18nFormatterProvider> namedFormatters = new HashMap<>(DEFAULT_NAMED_FORMATTERS);
    private final AggregatedI18nLoader loader = new AggregatedI18nLoader();
    private final List<I18nPath> prefixes = new ArrayList<>();
    private I18nKeyGenerator keyGenerator = relaxedI18nKeyGenerator();
    private I18nUnresolvedMessageHandler unresolvedMessageHandler = I18nUnresolvedMessageHandler.throwError();

    private I18nMessagePackBuilder copy() {
        I18nMessagePackBuilder builder = new I18nMessagePackBuilder();
        builder.typeFormatters.clear();
        builder.typeFormatters.putAll(typeFormatters);
        builder.namedFormatters.clear();
        builder.namedFormatters.putAll(namedFormatters);
        builder.loader.addLoader(loader.copy());
        builder.prefixes.clear();
        builder.prefixes.addAll(prefixes);
        builder.keyGenerator = keyGenerator;
        builder.unresolvedMessageHandler = unresolvedMessageHandler;
        return builder;
    }

    @NotNull
    public I18nMessagePackBuilder scanFileSystem(@NotNull String firstPattern, String... others) {
        expectNonBlank(firstPattern, "firstPattern");
        I18nLoader loader = I18nFileLoaderFactory.scanFileSystem(firstPattern, others);
        this.loader.addLoader(loader);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder scanFileSystem(@NotNull FileSystem fileSystem, @NotNull String firstPattern, String... others) {
        expectNonBlank(firstPattern, "firstPattern");
        expectNonNull(fileSystem, "fileSystem");
        I18nLoader loader = I18nFileLoaderFactory.scanFileSystem(fileSystem, firstPattern, others);
        this.loader.addLoader(loader);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder scanClassPath(@NotNull String firstPattern, String... others) {
        expectNonBlank(firstPattern, "firstPattern");
        return scanClassPath(Thread.currentThread().getContextClassLoader(), firstPattern, others);
    }

    @NotNull
    public I18nMessagePackBuilder scanClassPath(@NotNull ClassLoader classLoader, @NotNull String firstPattern, String... others) {
        expectNonBlank(firstPattern, "firstPattern");
        expectNonNull(classLoader, "classLoader");
        I18nLoader loader = I18nFileLoaderFactory.scanClassPath(classLoader, firstPattern, others);
        this.loader.addLoader(loader);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder addLoader(@NotNull I18nLoader loader) {
        expectNonNull(loader, "loader");
        this.loader.addLoader(loader);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder addMessage(@NotNull Locale locale, @NotNull I18nPath path, @NotNull String template) {
        expectNonNull(locale, "locale");
        expectNonNull(path, "path");
        expectNonBlank(template, "template");
        return addMessage(I18nKey.of(locale, path), template);
    }

    @NotNull
    public I18nMessagePackBuilder addMessage(@NotNull Locale locale, @NotNull String path, @NotNull String template) {
        expectNonNull(locale, "locale");
        expectNonBlank(path, "path");
        expectNonBlank(template, "template");
        return addMessage(I18nKey.of(locale, path), template);
    }

    @NotNull
    public I18nMessagePackBuilder addMessage(@NotNull I18nKey key, @NotNull String template) {
        expectNonNull(key, "key");
        expectNonBlank(template, "template");
        this.loader.addMessage(key, template);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder addMessages(@NotNull Map<I18nKey, String> messages) {
        expectNonNull(messages, "messages");
        this.loader.addMessages(messages);
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
        Map<I18nKey, String> entries = loader.load();
        return build(entries);
    }

    @NotNull
    public Reloadable18nMessagePack buildReloadable() {
        I18nMessagePackBuilder copy = this.copy();
        AggregatedI18nLoader loader = copy.loader;
        return new Reloadable18nMessagePack(loader, copy::build);
    }

    @NotNull
    public Reloadable18nMessagePack buildAndWatchForChanges() {
        Reloadable18nMessagePack messagePack = buildReloadable();
        messagePack.startWatching();
        return messagePack;
    }

    private I18nMessagePack build(Map<I18nKey, String> entries) {
        I18nMessageTemplatesPack templatePack = new I18nMessageTemplatesPack(entries, keyGenerator);
        FormatterResolver formatterResolver = new FormatterResolver(this.namedFormatters, this.typeFormatters, templatePack);
        MessageTemplateParser parser = new MessageTemplateParser(formatterResolver);
        Map<I18nKey, MessageTemplate> templates = parser.parseTemplates(templatePack);
        return new ImmutableI18nMessagePack(templates, parser, unresolvedMessageHandler, keyGenerator, prefixes);
    }

    private Map<I18nKey, MessageTemplate> parseTemplates(I18nMessageTemplatesPack messages, ExpressionParser parser) {
        Map<I18nKey, MessageTemplate> result = new HashMap<>();
        Map<String, Expression> expressions = new HashMap<>();
        for (Map.Entry<I18nKey, String> entry : messages.entries()) {
            I18nKey key = entry.getKey();
            String value = entry.getValue();
            Expression expression = expressions.computeIfAbsent(value, parser::parse);
            MessageTemplate template = new MessageTemplate(value, expression);
            result.put(key, template);
        }
        return result;
    }
}
